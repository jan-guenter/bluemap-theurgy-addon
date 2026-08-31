# BlueMap Theurgy Add-on

A Java 21 BlueMap 5.23 feature-backport add-on for the exact
`theurgy-1.76.0-mc1.21.1` profile in All the Mons `1.2.0` / Minecraft
`1.21.1`.

Status: owner-accepted `0.1.0-alpha.2` release candidate. The unchanged
five-apparatus visual scope targets only BlueMap feature-backport commit
`7e07f4e74ec1e92a6ead9aa1e66054af3e133aac` and API commit
`285c9a60eff3ac2b0cab308ce1058d1565be0971`. It still admits only the exact
Theurgy runtime and reads geometry and textures from that installed JAR.
Unsupported runtimes and blocks retain BlueMap's stock rendering.

## Build

Clone with `--recurse-submodules`, or initialize an existing checkout with:

```bash
git submodule update --init --recursive -- \
  tooling/bluemap-addon-toolkit modules/bluemap-addon-adapter-api
```

The settings preflight accepts only the committed toolkit and Adapter API
gitlinks. It rejects an uninitialized, changed, dirty, incorrectly pinned, or
source-tree-mismatched checkout.

```bash
gradle --no-daemon -PbluemapSourcePath=../bluemap-backport clean check build
```

`check` is the Java, checkstyle, and archive gate. `prototypeCheck` also
requires every exact candidate JAR property and validates the gallery. The
production and sources JARs contain the four exact Adapter API sources, never
the standalone module JAR. See `provenance/upstreams.json` for immutable input
identities and the [execution guide](docs/EXECUTION.md) for the review and
release loop.

## Install

Place the production JAR in BlueMap's add-on pack directory and restart the
BlueMap JVM. Removal plus one restart restores stock behavior. The add-on
creates no custom world state.

Set `-Dbluemap.theurgy.disabled=true` to leave the exact profile inactive.

## Scope boundary

The initial implementation must be limited to a small observed BlueMap defect.
Live contents, fill levels, activity overlays, particles, animation phase, and
unsupported states stay stock or deterministic-neutral unless the owner
explicitly expands scope.

No Theurgy binary, source, class, asset, captured mesh, or gallery is
bundled in the add-on.
