/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.theurgy.adapter.bluemap522;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector4f;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.bluecolored.bluemap.core.resources.ResourcePath;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockStateCondition;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.VariantSet;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variants;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Element;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Face;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Model;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Rotation;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.TextureVariable;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.Direction;
import de.bluecolored.bluemap.core.util.Key;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/** Compiles installed Bedrock geometry into ordinary BlueMap block models. */
final class InstalledGeoModels {

    private static final int MAX_GEOMETRY_BYTES = 1024 * 1024;
    private static final int MAX_CUBES = 512;
    private static final Key EMPTY_MODEL = Key.parse("bluemap_theurgy:block/empty");

    private InstalledGeoModels() {
    }

    static boolean install(ResourcePack pack, Path artifact) {
        Map<TheurgyCatalog.Route, Model> compiled = new LinkedHashMap<>();
        try (ZipFile zip = new ZipFile(artifact.toFile())) {
            for (TheurgyCatalog.Route route : TheurgyCatalog.ROUTES) {
                byte[] geometry = read(zip, route.geometryPath());
                compiled.put(route, parse(geometry, route.texture()));
            }
        } catch (IOException | RuntimeException exception) {
            return false;
        }

        pack.getModels().put(EMPTY_MODEL, new Model(new Element[0]));
        for (Map.Entry<TheurgyCatalog.Route, Model> entry : compiled.entrySet()) {
            TheurgyCatalog.Route route = entry.getKey();
            pack.getModels().put(route.staticModel(), entry.getValue());
            pack.getBlockStates().put(
                    route.block(),
                    route.doubleTall()
                            ? doubleTall(route.staticModel())
                            : single(route.staticModel())
            );
        }
        return true;
    }

    static Model parse(byte[] raw, Key texture) {
        JsonObject root = JsonParser.parseString(
                new String(raw, StandardCharsets.UTF_8)
        ).getAsJsonObject();
        if (!"1.12.0".equals(root.get("format_version").getAsString())) {
            throw new IllegalArgumentException("unsupported Bedrock geometry version");
        }
        JsonArray geometries = root.getAsJsonArray("minecraft:geometry");
        if (geometries == null || geometries.size() != 1) {
            throw new IllegalArgumentException("expected one Bedrock geometry");
        }
        JsonObject geometry = geometries.get(0).getAsJsonObject();
        JsonObject description = geometry.getAsJsonObject("description");
        int textureWidth = positive(description, "texture_width");
        int textureHeight = positive(description, "texture_height");
        JsonArray bones = geometry.getAsJsonArray("bones");
        if (bones == null) {
            throw new IllegalArgumentException("Bedrock geometry has no bones");
        }

        List<Element> elements = new ArrayList<>();
        for (JsonElement boneElement : bones) {
            JsonObject bone = boneElement.getAsJsonObject();
            requireZeroRotation(bone.get("rotation"), "bone rotation");
            boolean boneMirror = bool(bone, "mirror", false);
            JsonArray cubes = bone.getAsJsonArray("cubes");
            if (cubes == null) {
                continue;
            }
            for (JsonElement cubeElement : cubes) {
                if (elements.size() >= MAX_CUBES) {
                    throw new IllegalArgumentException("Bedrock cube budget exceeded");
                }
                elements.add(cube(
                        cubeElement.getAsJsonObject(), texture,
                        textureWidth, textureHeight, boneMirror
                ));
            }
        }
        if (elements.isEmpty()) {
            throw new IllegalArgumentException("Bedrock geometry is empty");
        }
        return new Model(elements.toArray(Element[]::new));
    }

    private static Element cube(
            JsonObject cube,
            Key texture,
            int textureWidth,
            int textureHeight,
            boolean boneMirror
    ) {
        float[] origin = vector(cube, "origin", true);
        float[] size = vector(cube, "size", true);
        float inflate = number(cube, "inflate", 0F);
        float[] uv = vector(cube, "uv", false);
        if (uv.length != 2) {
            throw new IllegalArgumentException("Bedrock cube UV is not box UV");
        }
        boolean mirror = bool(cube, "mirror", boneMirror);

        Vector3f from = new Vector3f(
                origin[0] + 8F - inflate,
                origin[1] - inflate,
                origin[2] + 8F - inflate
        );
        Vector3f to = new Vector3f(
                origin[0] + size[0] + 8F + inflate,
                origin[1] + size[1] + inflate,
                origin[2] + size[2] + 8F + inflate
        );
        Rotation rotation = rotation(cube);
        Map<Direction, Face> faces = faces(
                texture, uv[0], uv[1], size,
                textureWidth, textureHeight, mirror
        );
        return new Element(from, to, rotation, false, 0, faces);
    }

    private static Rotation rotation(JsonObject cube) {
        JsonElement value = cube.get("rotation");
        if (value == null || value.isJsonNull()) {
            return Rotation.ZERO;
        }
        float[] angles = vector(cube, "rotation", true);
        float[] pivot = vector(cube, "pivot", true);
        return new Rotation(
                new Vector3f(pivot[0] + 8F, pivot[1], pivot[2] + 8F),
                angles[0], angles[1], angles[2], false
        );
    }

    private static Map<Direction, Face> faces(
            Key texture,
            float u,
            float v,
            float[] size,
            int textureWidth,
            int textureHeight,
            boolean mirror
    ) {
        float width = size[0];
        float height = size[1];
        float depth = size[2];
        EnumMap<Direction, Face> result = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            Rect rectangle = switch (direction) {
                case DOWN -> new Rect(u + depth, v, width, depth);
                case UP -> new Rect(u + depth + width, v, width, depth);
                case WEST -> mirror
                        ? new Rect(u + depth + width, v + depth, depth, height)
                        : new Rect(u, v + depth, depth, height);
                case NORTH -> new Rect(u + depth, v + depth, width, height);
                case EAST -> mirror
                        ? new Rect(u, v + depth, depth, height)
                        : new Rect(u + depth + width, v + depth, depth, height);
                case SOUTH -> new Rect(
                        u + depth + depth + width, v + depth, width, height
                );
            };
            float left = normalize(rectangle.left(), textureWidth);
            float right = normalize(rectangle.left() + rectangle.width(), textureWidth);
            if (mirror) {
                float swap = left;
                left = right;
                right = swap;
            }
            Vector4f faceUv = new Vector4f(
                    left,
                    normalize(rectangle.top(), textureHeight),
                    right,
                    normalize(rectangle.top() + rectangle.height(), textureHeight)
            );
            result.put(direction, new Face(
                    faceUv,
                    new TextureVariable(new ResourcePath<Texture>(texture))
            ));
        }
        return result;
    }

    private static float normalize(float value, int dimension) {
        return value * 16F / dimension;
    }

    private static BlockState single(Key model) {
        return new BlockState(new Variants(
                new VariantSet[0],
                new VariantSet(new Variant(new ResourcePath<Model>(model)))
        ));
    }

    private static BlockState doubleTall(Key model) {
        VariantSet lower = new VariantSet(
                BlockStateCondition.property("half", "lower"),
                new Variant(new ResourcePath<Model>(model))
        );
        VariantSet upper = new VariantSet(
                BlockStateCondition.property("half", "upper"),
                new Variant(new ResourcePath<Model>(EMPTY_MODEL))
        );
        return new BlockState(new Variants(new VariantSet[] {lower, upper}, null));
    }

    private static byte[] read(ZipFile zip, String path) throws IOException {
        ZipEntry entry = zip.getEntry(path);
        if (entry == null || entry.isDirectory() || entry.getSize() > MAX_GEOMETRY_BYTES) {
            throw new IOException("required installed geometry is unavailable");
        }
        try (InputStream input = zip.getInputStream(entry)) {
            byte[] result = input.readNBytes(MAX_GEOMETRY_BYTES + 1);
            if (result.length > MAX_GEOMETRY_BYTES) {
                throw new IOException("installed geometry exceeds byte budget");
            }
            return result;
        }
    }

    private static float[] vector(JsonObject object, String key, boolean triple) {
        JsonArray array = object.getAsJsonArray(key);
        int length = triple ? 3 : 2;
        if (array == null || array.size() != length) {
            throw new IllegalArgumentException("invalid vector: " + key);
        }
        float[] result = new float[length];
        for (int index = 0; index < length; index++) {
            result[index] = finite(array.get(index).getAsFloat(), key);
        }
        return result;
    }

    private static void requireZeroRotation(JsonElement value, String label) {
        if (value == null || value.isJsonNull()) {
            return;
        }
        JsonArray array = value.getAsJsonArray();
        if (array.size() != 3) {
            throw new IllegalArgumentException(label + " is invalid");
        }
        for (JsonElement element : array) {
            if (finite(element.getAsFloat(), label) != 0F) {
                throw new IllegalArgumentException(label + " is unsupported");
            }
        }
    }

    private static int positive(JsonObject object, String key) {
        int value = object.get(key).getAsInt();
        if (value <= 0) {
            throw new IllegalArgumentException(key + " is not positive");
        }
        return value;
    }

    private static float number(JsonObject object, String key, float fallback) {
        JsonElement value = object.get(key);
        return value == null ? fallback : finite(value.getAsFloat(), key);
    }

    private static float finite(float value, String label) {
        if (!Float.isFinite(value)) {
            throw new IllegalArgumentException(label + " is not finite");
        }
        return value;
    }

    private static boolean bool(JsonObject object, String key, boolean fallback) {
        JsonElement value = object.get(key);
        return value == null ? fallback : value.getAsBoolean();
    }

    private record Rect(float left, float top, float width, float height) {
    }
}
