#!/usr/bin/env zsh
#MISE description="Generate compose navigation graph model, Mermaid diagram, and interactive HTML preview"

set -euo pipefail

./gradlew :frontend:apps-android:compileDebugKotlin :frontend:apps-android:inferNavEdges --console=plain
python3 frontend/apps/android/build/export_assets.py
echo "Navigation graph assets successfully generated in frontend/.agents/plans/2026-11-24/"
