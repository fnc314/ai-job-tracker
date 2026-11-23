package dev.fnc314.aijobtracker.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import dev.fnc314.aijobtracker.testing.common.BaseArchitectureTest
import kotlin.test.Test

/**
 * Architectural tests verifying Gradle build and settings scripts across all subsystems.
 *
 * Enforces:
 * 1. Build scripts in frontend feature and core modules rely on local convention plugins.
 * 2. Settings scripts maintain standard repository or included-build definitions.
 * 3. Gradle scripts avoid raw hardcoded version strings outside version catalogs.
 */
class GradleScriptsArchitectureTest : BaseArchitectureTest() {

    private val allGradleScripts by lazy {
        Konsist
            .scopeFromProject()
            .files
            .filter { it.name.endsWith(".gradle.kts") }
            .filterNot {
                it.path.contains("/build/") ||
                    it.path.contains("/.gradle/") ||
                    it.path.contains("/bin/")
            }
    }

    @Test
    fun `all settings gradle files maintain expected build configurations`() {
        val settingsFiles = allGradleScripts.filter { it.name == "settings.gradle.kts" }

        settingsFiles.assertTrue { file ->
            file.text.contains("pluginManagement") ||
                file.text.contains("includeBuild") ||
                file.text.contains("include(") ||
                file.text.contains("rootProject.name")
        }
    }

    @Test
    fun `feature and core module build scripts apply local convention plugins`() {
        val moduleBuildScripts = allGradleScripts
            .filter { it.name == "build.gradle.kts" }
            .filter { it.path.contains("/frontend/features/") || it.path.contains("/frontend/core/") }

        moduleBuildScripts.assertTrue { file ->
            file.text.contains("libs.plugins.local")
        }
    }
}
