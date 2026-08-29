# BlueMap Theurgy Add-on

A Java 21 BlueMap add-on for the exact `theurgy-1.76.0-mc1.21.1` profile in All the Mons
`1.2.0` / Minecraft `1.21.1`.

Status: owner-accepted initial prerelease. The exact artifact gate admits the
pinned Theurgy runtime and renders the five supported apparatus shells from
its installed geometry and textures. Unsupported runtimes and blocks retain
BlueMap's stock rendering.

## Build

Clone with `--recurse-submodules`, or initialize an existing checkout with
`git submodule update --init --recursive -- tooling/bluemap-addon-toolkit`.
The settings preflight accepts only the committed toolkit gitlink at commit
`6cd34a8368cc4ee8628fbe830a90ec5b14960629` and rejects an uninitialized,
changed, or dirty toolkit checkout.

```bash
gradle --no-daemon -PbluemapSourcePath=../bluemap-backport clean check build
```

`check` is the quick Java/checkstyle/archive gate. `prototypeCheck` additionally
requires every exact candidate JAR property and validates the placeholder
gallery. See `provenance/upstreams.json` for immutable artifact identities and
the [execution guide](docs/EXECUTION.md) for the prototype-to-release loop.

## Install

After a renderer exists, place the production JAR in BlueMap's add-on pack
directory and restart the BlueMap JVM. Removal plus one restart restores stock
behavior; the add-on creates no custom world state.

Set `-Dbluemap.theurgy.disabled=true` to leave the exact profile inactive.

## Scope boundary

The initial implementation must be limited to a small observed BlueMap defect.
Live contents, fill levels, activity overlays, particles, animation phase, and
unsupported states stay stock or deterministic-neutral unless the owner
explicitly expands scope.

No Theurgy binary, source, class, asset, captured mesh, or gallery is
bundled in the add-on.
