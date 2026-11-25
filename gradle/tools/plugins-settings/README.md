# Subsystem Settings Plugin (`gradletools.settings.subsystem`)

Convention settings plugin for multi-subsystem Gradle builds in this repository.

## Overview

When building multi-composite or multi-repo Gradle architectures, sub-builds often need access to centralized version catalogs (`libs`, `local`, `tools`) and shared convention plugins. This plugin orchestrates:

1. Automatic resolution of the monorepo / project root.
2. Configuration of plugin and dependency resolution repositories (`google()`, `mavenCentral()`, `gradlePluginPortal()`).
3. Loading of requested Version Catalogs into `dependencyResolutionManagement`.
4. Inclusion of shared tool builds into `pluginManagement`.

## Usage in `settings.gradle.kts`

```kotlin
plugins {
    id("dev.fnc314.aijobtracker.gradletools.settings.subsystem") version "3.4.2"
}

subsystemSettings {
    // Optional override. By default, auto-resolved via:
    // 1. GRADLE_DOCS_ROOT environment variable
    // 2. WORKSPACE_ROOT environment variable
    // 3. Upwards filesystem traversal for gradle/wrapper/gradle-wrapper.properties
    // projectRoot.set(file("/path/to/root"))

    catalogs {
        // Abbreviated syntax: register("name") implicitly maps to "gradle/name.versions.toml"
        register("libs")
        register("local")
        register("tools")
    }

    toolBuilds {
        include("gradle/tools/plugins-projects")
        include("gradle/tools/plugins-docs")
    }
}
```

## Environment Variables

- `GRADLE_DOCS_ROOT`: Primary path override for standalone extraction and documentation pipelines.
- `WORKSPACE_ROOT`: Standard continuous integration workspace path override.
