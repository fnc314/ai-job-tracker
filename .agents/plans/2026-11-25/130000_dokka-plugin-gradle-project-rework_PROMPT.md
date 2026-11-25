/plan
<https://www.fnc314.dev/project-collections-gradle-settings-plugin/dokka/project-collections-gradle-settings-plugin/dev.fnc314.gradle.plugins.settings.projectcollectionsgradlesettingsplugin/-project-collections-gradle-settings-extension/flatten-middle-projects.html>

Right now, this plugin flattens the project hierarchy to remove empty projects, but the `dokka` configuration requires each "layer" to apply the plugin. This means the formerly empty projects are no longer "empty". When we toggle this flag in the plugin, all of the exisitng mise scripts and existing functionality will break because projects will go from `:frontend:apps-android` to `:frontend:apps:android`, among other changes. Also, this will give us a chance to remove calls to `project().projectDir` in `settings.gradle.kts` files (projects should align with physical location on the drive).

I want the dokka docs to reflect the project structure more stictly, and to achieve this we will configure Dokka in 3 distinct layers to align with the general project layout.

## Dokka Plugin Layers

1. `Root` - This is the general root and is applied to @build.gradle.kts at the root of the repository. It will configure the main representation of the documentation (i.e. the thing the viewer sees first) and aggregate the various `includeBuild` paths from @settings.gradle.kts at the root of the repository.
2. `Node` - This is a more transient object that does not contribute artifacts or publications in general but acts as an intermediary between various `Leaf` instances. Examples would be `:frontend`, `:frontend:apps`, `:frontend:features`, `frontend:features:sign-in`, etc.... Configurations to `dokka` here are little to none and most work goes into `build.gradle.kts` files. We'll leverage @gradle/local.versions.toml to define `local.bundles` to simplify the necessary `dependencies.add("dokka", local.bundles.someLeafProject)`.
3. `Leaf` - This is the project which produces rendered documentation. Here we will configure Dokka to bind to 3rd party documentation sites (Gradle, Kotlin, Android, etc...) and potentially customize the "`dokka` module name" for clarity (on the first pass, do _not_ configure this name, it should be able to adopt the same from `Gradle`).

### Gotchas

- Because we're going to be including a bunch of `:ui`, `:data`, and `:domain` projects, I'm not sure if the path is sufficient enough for Gradle that each project be uniquely named. Should gradle error, I want to work through my options before continuing
- All work should exist within a dedicated module within @gradle/tools/project-plugins/src/main/.../plugins/configs/dokka. Ignore existing plugin and create net-new representations for these definitions

## Adjacent work

1. Create @gradle/tools.versions.toml and populate it with the `[version]`, `[libraries]`, `[bundles]`, and `[plugins]` tables matching the default format of `libs.versions.toml`
2. Migrate from @gradle/libs.versions.toml the declarations related to @gradle/tools/project-plugins and put it in @gradle/tools.versions.toml
3. Draft new names and identities for this work within @gradle/tools.versions.toml
4. Align versions across the repo and @gradle/local.versions.toml and @gradle/tools.versions.toml

## End result

When we generate the dokka site, today, all of the projects are flat listed on the left-hand side and the same goes for the Gradle tool in an IDE. I'd like them to mirror the actual folder/file structure from whence they are generated. I also want the names of projects and folders to be one-to-one forever and always. All `mise` tasks will be migrated to the new project names and any entry within @.agents and across `*.md` files (collect old `/plans/` with project references for audit before editing) especially in repo root as well.

Ask clarifying questions before drafting final proposal. Plan to use `/agents` for one pass go and consider the `Elephants Goldfish` approach.
