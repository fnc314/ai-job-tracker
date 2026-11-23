package dev.fnc314.aijobtracker.testing.common

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.container.KoScope
import java.io.File

/**
 * Base fixture for Konsist architecture and compliance tests, resolving the workspace root
 * and providing scoped access across all monorepo subsystems.
 */
public abstract class BaseArchitectureTest {

    /**
     * Resolves the workspace root directory containing `mise.toml`.
     */
    public val rootDir: File = generateSequence(File("").absoluteFile) { it.parentFile }
        .first { File(it, "mise.toml").exists() }

    /**
     * Scoped access to the `records` subsystem.
     */
    public val recordsScope: KoScope by lazy {
        Konsist.scopeFromDirectory("records")
    }

    /**
     * Scoped access to the `backend` subsystem.
     */
    public val backendScope: KoScope by lazy {
        Konsist.scopeFromDirectory("backend")
    }

    /**
     * Scoped access to the `frontend` subsystem.
     */
    public val frontendScope: KoScope by lazy {
        Konsist.scopeFromDirectory("frontend")
    }

    /**
     * Scoped access to `gradle/tools`.
     */
    public val gradleToolsScope: KoScope by lazy {
        Konsist.scopeFromDirectory("gradle/tools")
    }
}
