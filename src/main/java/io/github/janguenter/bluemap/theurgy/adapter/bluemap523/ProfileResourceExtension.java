/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.theurgy.adapter.bluemap523;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePackExtension;
import de.bluecolored.bluemap.core.util.Key;
import io.github.janguenter.bluemap.theurgy.activation.AddonRuntime;
import io.github.janguenter.bluemap.theurgy.profile.ExactArtifactDetector;
import io.github.janguenter.bluemap.theurgy.profile.Theurgy1760Profile;

import java.nio.file.Path;
import java.util.Set;

/** Exact-artifact admission hook for installed Theurgy apparatus geometry. */
final class ProfileResourceExtension implements ResourcePackExtension {

    private final ResourcePack resourcePack;
    private final AddonRuntime runtime;

    ProfileResourceExtension(ResourcePack resourcePack, AddonRuntime runtime) {
        this.resourcePack = resourcePack;
        this.runtime = runtime;
    }

    @Override
    public void loadResources(Iterable<Path> roots) {
        if (Boolean.getBoolean("bluemap.theurgy.disabled")) {
            runtime.inactive("operator-disabled");
            return;
        }
        Path artifact = ExactArtifactDetector.findExact(
                roots, Theurgy1760Profile.ARTIFACTS.getFirst()
        );
        if (artifact == null) {
            runtime.inactive("exact-artifact-missing-or-duplicate");
            return;
        }
        if (!InstalledGeoModels.install(resourcePack, artifact)) {
            runtime.inactive("required-installed-resource-missing");
            return;
        }
        runtime.activate();
    }

    @Override
    public Set<Key> collectUsedTextureKeys() {
        return runtime.active() ? TheurgyCatalog.textureKeys() : Set.of();
    }

    @Override
    public void bake() {
        if (runtime.active()) {
            System.out.println("BlueMap Theurgy add-on active: 5 apparatus shells.");
        }
    }
}
