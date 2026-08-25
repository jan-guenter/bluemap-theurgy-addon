/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.theurgy.adapter.bluemap522;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class InstalledGeoModelsTest {

    @Test
    void compilesEveryPinnedGeometry() throws Exception {
        String artifact = System.getProperty("theurgyJar");
        assumeTrue(artifact != null && !artifact.isBlank());

        try (ZipFile zip = new ZipFile(Path.of(artifact).toFile())) {
            for (TheurgyCatalog.Route route : TheurgyCatalog.ROUTES) {
                byte[] raw = zip.getInputStream(zip.getEntry(route.geometryPath())).readAllBytes();
                assertNotNull(InstalledGeoModels.parse(raw, route.texture()));
            }
        }
    }
}
