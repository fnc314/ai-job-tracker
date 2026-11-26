package dev.fnc314.aijobtracker.gradletools.docs.tree

import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.plugability.DokkaContext
import org.jetbrains.dokka.renderers.PostAction
import java.io.File

/**
 * PostAction hook executing at the completion of HTML documentation generation.
 *
 * Restructures navigation sidebar HTML and the main module table into a nested tree.
 */
public class NavigationTreePostAction(
  private val context: DokkaContext,
) : PostAction {
  override fun invoke() {
    val outputDir = context.configuration.outputDir
    val modules = context.configuration.modules
    if (modules.isEmpty() || !outputDir.exists()) {
      return
    }

    NavigationTreeGenerator.process(outputDir, modules)
  }
}
