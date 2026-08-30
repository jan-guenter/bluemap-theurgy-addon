/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.theurgy.adapter.bluemap523;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.util.Key;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.RegistryGuard;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.ResourceExtensionType;
import io.github.janguenter.bluemap.theurgy.activation.AddonRuntime;

/** Exact BlueMap 5.23 feature-backport registration boundary. */
public final class BlueMap523Adapter {

    private static final AddonRuntime RUNTIME = AddonRuntime.INSTANCE;
    private static final Key EXTENSION_KEY =
            Key.parse("bluemap_theurgy:exact_profile");
    private static final ResourcePack.Extension<ProfileResourceExtension> EXTENSION =
            new ResourceExtensionType<>(
                    EXTENSION_KEY,
                    pack -> new ProfileResourceExtension(pack, RUNTIME)
            );

    private BlueMap523Adapter() {
    }

    /** Registers only the safe exact-profile probe in the generated seed. */
    public static synchronized boolean install() {
        if (!RegistryGuard.canRegister(ResourcePack.Extension.REGISTRY, EXTENSION)) {
            RUNTIME.fail("registry-collision");
            return false;
        }
        if (!RegistryGuard.register(ResourcePack.Extension.REGISTRY, EXTENSION)) {
            RUNTIME.fail("registry-registration-failed");
            return false;
        }
        return true;
    }

    static ResourcePack.Extension<ProfileResourceExtension> extension() {
        return EXTENSION;
    }
}
