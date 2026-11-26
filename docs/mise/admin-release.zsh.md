## `admin:release`

Bump version in gradle/local.versions.toml, create git tag (version/X.Y.Z), and push by default

- **Usage:** `admin:release [FLAGS] [version]`
- **Aliases:** `release`

### Arguments

- **`[version]`** — Semantic version to release (e.g. 1.0.0)

### Flags

- **`-n --no-push`** — Skip push, creating commit and tag locally only
- **`--suppress-auto-push`** — Alias to skip push, creating commit and tag locally only
- **`--commit-only`** — Alias to skip push, creating commit and tag locally only
