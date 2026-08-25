/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.theurgy.adapter.bluemap522;

import de.bluecolored.bluemap.core.util.Key;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Exact installed-resource routes for Theurgy's static GeckoLib apparatus shells. */
final class TheurgyCatalog {

    static final List<Route> ROUTES = List.of(
            route(
                    "sal_ammoniac_accumulator",
                    "sal_ammoniac_accumulator",
                    "sal_ammoniac_accumulator",
                    false
            ),
            route(
                    "sal_ammoniac_tank",
                    "sal_ammoniac_tank",
                    "sal_ammoniac_tank",
                    false
            ),
            vessel("incubator_mercury_vessel", "incubator_vessel_gold"),
            vessel("incubator_sulfur_vessel", "incubator_vessel_iron"),
            vessel("incubator_salt_vessel", "incubator_vessel_bronze")
    );

    private TheurgyCatalog() {
    }

    static Set<Key> textureKeys() {
        LinkedHashSet<Key> result = new LinkedHashSet<>();
        for (Route route : ROUTES) {
            result.add(route.texture());
        }
        return Set.copyOf(result);
    }

    private static Route vessel(String block, String texture) {
        return new Route(
                Key.parse("theurgy:" + block),
                Key.parse("theurgy:block/incubator_vessel"),
                Key.parse("bluemap_theurgy:block/" + block),
                "assets/theurgy/geo/incubator_vessel.geo.json",
                Key.parse("theurgy:block/" + texture),
                false
        );
    }

    private static Route route(
            String block, String model, String texture, boolean doubleTall
    ) {
        return new Route(
                Key.parse("theurgy:" + block),
                Key.parse("theurgy:block/" + model),
                Key.parse("bluemap_theurgy:block/" + block),
                "assets/theurgy/geo/" + model + ".geo.json",
                Key.parse("theurgy:block/" + texture),
                doubleTall
        );
    }

    record Route(
            Key block,
            Key originalModel,
            Key staticModel,
            String geometryPath,
            Key texture,
            boolean doubleTall
    ) {
    }
}
