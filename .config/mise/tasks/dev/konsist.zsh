#!/usr/bin/env zsh
#MISE description="Run Konsist architectural and standards test suites across all subsystems"
#MISE alias="konsist"
set -euo pipefail

cd "${MISE_PROJECT_ROOT}"
exec "${MISE_PROJECT_ROOT}/gradlew" :tests:architecture:test :tests:standards:test "$@"

