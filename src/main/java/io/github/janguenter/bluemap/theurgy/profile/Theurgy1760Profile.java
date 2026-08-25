/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.theurgy.profile;

import java.util.List;

/** Exact All the Mons 1.2.0 profile `theurgy-1.76.0-mc1.21.1`. */
public final class Theurgy1760Profile {

    public static final String PROFILE_ID = "theurgy-1.76.0-mc1.21.1";
    public static final List<ArtifactPin> ARTIFACTS = List.of(
            new ArtifactPin(
                    "theurgy",
                    "theurgy",
                    "1.76.0",
                    "theurgy-1.21.1-neoforge-1.76.0.jar",
                    5_324_349L,
                    "c4bc955e30f1155b83954a0c2aba80adf19f72e6f5d95cfd8d72e5afb5e60d8f"
            )
    );

    private Theurgy1760Profile() {
    }
}
