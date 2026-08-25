#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Bounded comparison gallery for Theurgy's five static apparatus shells."""

from __future__ import annotations

from dataclasses import dataclass


NAMESPACE = "theurgy_gallery"
ENVELOPE = (173, 99, 173, 188, 103, 183)


@dataclass(frozen=True)
class Placement:
    case_id: str
    label: str
    x: int
    y: int
    z: int
    block_state: str
    expected: str


PLACEMENTS = (
    Placement(
        "sal-ammoniac-accumulator",
        "sal ammoniac accumulator static shell",
        176,
        100,
        176,
        "theurgy:sal_ammoniac_accumulator",
        "installed-geometry-visible",
    ),
    Placement(
        "sal-ammoniac-tank",
        "sal ammoniac tank static shell",
        180,
        100,
        176,
        "theurgy:sal_ammoniac_tank",
        "installed-geometry-visible",
    ),
    Placement(
        "incubator-mercury-vessel",
        "incubator mercury vessel static shell",
        184,
        100,
        176,
        "theurgy:incubator_mercury_vessel",
        "installed-geometry-visible",
    ),
    Placement(
        "incubator-sulfur-vessel",
        "incubator sulfur vessel static shell",
        176,
        100,
        180,
        "theurgy:incubator_sulfur_vessel",
        "installed-geometry-visible",
    ),
    Placement(
        "incubator-salt-vessel",
        "incubator salt vessel static shell",
        180,
        100,
        180,
        "theurgy:incubator_salt_vessel",
        "installed-geometry-visible",
    ),
    Placement(
        "stock-control",
        "stone stock rendering control",
        184,
        100,
        180,
        "minecraft:stone",
        "stock-visible",
    ),
)
