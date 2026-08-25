# Theurgy comparison gallery

This gallery places the five non-animated Theurgy apparatus shells whose stock
models contain no geometry, plus one `minecraft:stone` control. The add-on
compiles only their installed geometry and texture resources; it does not own
fluids, contents, animations, logistics wires, or machine state.

Use the stable deterministic commands:

```bash
python gallery/generate.py
python gallery/generate.py --check
python gallery/lint.py
bash gallery/package.sh /tmp/theurgy-gallery.zip
```

The release gate rejects any prototype placeholder marker in `cases.py`. Keep
gallery generation deterministic, bounded, synthetic where practical, and
free of candidate assets or captured meshes.
