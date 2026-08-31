# Changelog

## 0.1.0-alpha.2 - 2026-08-30

- Target only BlueMap feature-backport commit
  `7e07f4e74ec1e92a6ead9aa1e66054af3e133aac` and API commit
  `285c9a60eff3ac2b0cab308ce1058d1565be0971`.
- Move the local adapter boundary from `bluemap522` to `bluemap523`.
- Compile the four shared bootstrap helpers from Adapter API
  `0.1.0-alpha.2` and remove the three duplicate local helpers.
- Keep the exact Theurgy profile, five apparatus routes, gallery, geometry
  compiler, installed-resource use, and stock fallback unchanged.

## 0.1.0-alpha.1 - 2026-08-25

- Added an exact-gated Java 21 renderer for the five supported apparatus
  shells in `theurgy-1.76.0-mc1.21.1`.
- Compiled the installed Bedrock geometry with box and mapped UV support.
- Kept unsupported artifacts and blocks on BlueMap's stock rendering path.
- Added the deterministic five-shell gallery and owner-accepted release
  provenance.
