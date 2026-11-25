## `admin:release`

Bump version in gradle/local.versions.toml, create git tag (version/X.Y.Z), and prompt to push

- **Usage:** `admin:release [FLAGS] [version]`
- **Aliases:** `release`

### Arguments

- **`[version]`** — Semantic version to release (e.g. 1.0.0)

### Flags

- **`-p --push`** — Push the commit and created version tag to origin without prompt
- **`-n --no-push`** — Skip push and prompt, creating commit and tag locally only
- **`--suppress-auto-push`** — Alias to skip push and prompt, creating commit and tag locally only
