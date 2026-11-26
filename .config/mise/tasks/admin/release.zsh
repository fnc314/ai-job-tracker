#!/usr/bin/env zsh
#MISE description="Bump version in gradle/local.versions.toml, create git tag (version/X.Y.Z), and push by default"
#MISE alias="release"
#USAGE arg "[version]" help="Semantic version to release (e.g. 1.0.0)"
#USAGE flag "-n --no-push" help="Skip push, creating commit and tag locally only"
#USAGE flag "--suppress-auto-push" help="Alias to skip push, creating commit and tag locally only"
#USAGE flag "--commit-only" help="Alias to skip push, creating commit and tag locally only"

set -euo pipefail

TOML_FILE="gradle/local.versions.toml"
PROPERTIES_FILE="gradle.properties"
VERSION=""
NO_PUSH=false

for arg in "$@"; do
    case "$arg" in
        -n|--no-push|--suppress-auto-push|--commit-only)
            NO_PUSH=true
            ;;
        -p|--push)
            # Retained for backwards compatibility; push is now the default
            ;;
        -*)
            ;;
        *)
            if [[ -z "$VERSION" ]]; then
                VERSION="$arg"
            fi
            ;;
    esac
done

CURRENT_TOML_VERSION=$(sed -n -E 's/^[[:space:]]*version[[:space:]]*=[[:space:]]*"([^"]+)"/\1/p' "$TOML_FILE" | head -n1)
LATEST_GIT_TAG=$(git tag -l "version/*" | tr -s '[:space:]' '\n' | sort -V | tail -n1 | sed 's|^version/||')

if [[ -z "$VERSION" ]]; then
    echo "Current toml version: ${CURRENT_TOML_VERSION:-None}"
    echo "Current highest git tag: ${LATEST_GIT_TAG:-None}"
    read -r "VERSION?Enter new version to release (e.g. 3.3.0): "
fi

VERSION="${VERSION#version/}"
VERSION="${VERSION#v}"

if [[ ! "$VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    echo "❌ Error: Version '$VERSION' does not follow semantic versioning (X.Y.Z, e.g. 1.0.0)." >&2
    exit 1
fi

TAG_NAME="version/$VERSION"

if git rev-parse "$TAG_NAME" >/dev/null 2>&1; then
    echo "❌ Error: Git tag '$TAG_NAME' already exists." >&2
    exit 1
fi

# Ensure working tree is clean, or only gradle/local.versions.toml and gradle.properties are dirty
UNCOMMITTED=$(git status --porcelain | grep -v "$TOML_FILE" | grep -v "$PROPERTIES_FILE" || true)
if [[ -n "$UNCOMMITTED" ]]; then
    echo "⚠️ Warning: Working tree has uncommitted changes outside of $TOML_FILE and $PROPERTIES_FILE:" >&2
    git status -s >&2
    echo "Please commit or stash your changes before tagging a release." >&2
    exit 1
fi

# Update gradle/local.versions.toml and gradle.properties
if [[ -f "$TOML_FILE" ]]; then
    echo "📝 Updating $TOML_FILE to version = \"$VERSION\"..."
    # macOS-safe sed in-place
    sed -i '' -E "s/^[[:space:]]*version[[:space:]]*=[[:space:]]*\"[^\"]+\"/  version = \"$VERSION\"/" "$TOML_FILE"
else
    echo "❌ Error: $TOML_FILE not found." >&2
    exit 1
fi

if [[ -f "$PROPERTIES_FILE" ]]; then
    echo "📝 Updating $PROPERTIES_FILE to version=$VERSION..."
    sed -i '' -E "s/^[[:space:]]*version[[:space:]]*=[[:space:]]*.*/version=$VERSION/" "$PROPERTIES_FILE"
fi

git add "$TOML_FILE" "$PROPERTIES_FILE"
git commit -m "chore(release): bump version to $VERSION"
echo "✓ Committed $TOML_FILE and $PROPERTIES_FILE bump."

BRANCH=$(git rev-parse --abbrev-ref HEAD)
echo "🚀 Creating git tag '$TAG_NAME' on branch '$BRANCH'..."
git tag -a "$TAG_NAME" -m "Release version $VERSION"
echo "✓ Created annotated tag: $TAG_NAME"

if [[ "$NO_PUSH" == true ]]; then
    echo ""
    echo "Tag created locally (--no-push/--commit-only specified). When ready to publish:"
    echo "   git push origin $BRANCH"
    echo "   git push origin $TAG_NAME"
else
    echo "Pushing '$BRANCH' and '$TAG_NAME' to origin..."
    git push origin "$BRANCH"
    git push origin "$TAG_NAME"
    echo "✓ Pushed commit and tag to remote origin."
fi


