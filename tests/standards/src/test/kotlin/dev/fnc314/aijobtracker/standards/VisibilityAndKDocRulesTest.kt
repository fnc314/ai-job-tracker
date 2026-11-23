package dev.fnc314.aijobtracker.standards

import com.lemonappdev.konsist.api.verify.assertTrue
import kotlin.test.Test
import com.lemonappdev.konsist.api.declaration.KoInterfaceDeclaration
import com.lemonappdev.konsist.api.provider.KoContainingDeclarationProvider
import com.lemonappdev.konsist.api.provider.modifier.KoOverrideModifierProvider

import com.lemonappdev.konsist.api.provider.modifier.KoActualModifierProvider
import com.lemonappdev.konsist.api.provider.modifier.KoExpectModifierProvider
import dev.fnc314.aijobtracker.testing.common.BaseArchitectureTest

/**
 * Repository-wide architectural tests enforcing explicit visibility modifiers (aligning with `explicitApi`)
 * and KDoc documentation priorities.
 */
class VisibilityAndKDocRulesTest : BaseArchitectureTest() {

    @Test
    fun `all public declarations and interfaces across repository have KDoc including expect declarations`() {
        val allScopes = listOf(recordsScope, backendScope, gradleToolsScope, frontendScope)

        for (scope in allScopes) {
            val decls = scope.classes() +
                scope.interfaces() +
                scope.objects() +
                scope.functions() +
                scope.properties()

            decls
                .filter { it.hasPublicModifier || (it is KoInterfaceDeclaration && it.hasPublicModifier) }
                .filterNot {
                    it.containingFile.path.contains("/test/") ||
                        it.containingFile.path.contains("/jvmTest/") ||
                        it.containingFile.path.contains("/androidTest/") ||
                        it.containingFile.path.contains("/commonTest/") ||
                        it.containingFile.path.contains("/build/") ||
                        it.containingFile.path.contains("/bin/")
                }
                .filterNot { (it as? KoActualModifierProvider)?.hasActualModifier == true }
                .filterNot { (it as? KoOverrideModifierProvider)?.hasOverrideModifier == true }
                .assertTrue { it.hasKDoc }
        }
    }

    @Test
    fun `all declarations in records have explicit visibility modifiers`() {
        val decls = recordsScope.classes() +
            recordsScope.interfaces() +
            recordsScope.objects() +
            recordsScope.functions() +
            recordsScope.properties()

        decls
            .filterNot {
                it.containingFile.path.contains("/test/") ||
                    it.containingFile.path.contains("/jvmTest/") ||
                    it.containingFile.path.contains("/build/") ||
                    it.containingFile.path.contains("/bin/")
            }
            .assertTrue {
                it.hasPublicModifier || it.hasInternalModifier || it.hasPrivateModifier || it.hasProtectedModifier ||
                    (it as? KoOverrideModifierProvider)?.hasOverrideModifier == true ||
                    (it as? KoContainingDeclarationProvider)?.containingDeclaration is KoInterfaceDeclaration
            }
    }

    @Test
    fun `all declarations in backend have explicit visibility modifiers`() {
        val decls = backendScope.classes() +
            backendScope.interfaces() +
            backendScope.objects() +
            backendScope.functions() +
            backendScope.properties()

        decls
            .filterNot {
                it.containingFile.path.contains("/test/") ||
                    it.containingFile.path.contains("/jvmTest/") ||
                    it.containingFile.path.contains("/build/") ||
                    it.containingFile.path.contains("/bin/")
            }
            .assertTrue {
                it.hasPublicModifier || it.hasInternalModifier || it.hasPrivateModifier || it.hasProtectedModifier ||
                    (it as? KoOverrideModifierProvider)?.hasOverrideModifier == true ||
                    (it as? KoContainingDeclarationProvider)?.containingDeclaration is KoInterfaceDeclaration
            }
    }

    @Test
    fun `all declarations in gradle tools have explicit visibility modifiers`() {
        val decls = gradleToolsScope.classes() +
            gradleToolsScope.interfaces() +
            gradleToolsScope.objects() +
            gradleToolsScope.functions() +
            gradleToolsScope.properties()

        decls
            .filterNot {
                it.containingFile.path.contains("/test/") ||
                    it.containingFile.path.contains("/jvmTest/") ||
                    it.containingFile.path.contains("/build/") ||
                    it.containingFile.path.contains("/bin/")
            }
            .assertTrue {
                it.hasPublicModifier || it.hasInternalModifier || it.hasPrivateModifier || it.hasProtectedModifier ||
                    (it as? KoOverrideModifierProvider)?.hasOverrideModifier == true ||
                    (it as? KoContainingDeclarationProvider)?.containingDeclaration is KoInterfaceDeclaration
            }
    }

    @Test
    fun `audit internal declarations KDoc coverage across repository`() {
        val allScopes = listOf(recordsScope, backendScope, gradleToolsScope, frontendScope)
        var totalInternal = 0
        var internalWithKDoc = 0

        for (scope in allScopes) {
            val decls = scope.classes() +
                scope.interfaces() +
                scope.objects() +
                scope.functions() +
                scope.properties()

            val internalDecls = decls
                .filter { it.hasInternalModifier }
                .filterNot {
                    it.containingFile.path.contains("/test/") ||
                        it.containingFile.path.contains("/jvmTest/") ||
                        it.containingFile.path.contains("/androidTest/") ||
                        it.containingFile.path.contains("/build/")
                }

            totalInternal += internalDecls.size
            internalWithKDoc += internalDecls.count { it.hasKDoc }
        }

        println("Internal declarations KDoc coverage: $internalWithKDoc / $totalInternal (${if (totalInternal > 0) (internalWithKDoc * 100 / totalInternal) else 0}%)")
    }
}