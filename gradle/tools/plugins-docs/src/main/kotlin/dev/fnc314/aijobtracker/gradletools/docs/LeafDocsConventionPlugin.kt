package dev.fnc314.aijobtracker.gradletools.docs

import org.gradle.api.Project
import org.jetbrains.dokka.gradle.DokkaExtension
import java.io.File

/**
 * Leaf documentation convention plugin applied to projects containing source code.
 *
 * Configures documentation generation, binds external API documentation links,
 * and sets up frontpiece README inclusions.
 */
public class LeafDocsConventionPlugin : DocsConventionPlugin() {
  override fun Project.configureDocs() {
    pluginManager.apply("org.jetbrains.dokka")

    extensions.configure(DokkaExtension::class.java) { dokka ->
      // Default to Gradle project name on first pass
      val modName = name
      dokka.dokkaPublications.named("html") { publication ->
        publication.moduleName.set(modName)
      }

      val readme = file("README.md")
      if (readme.exists()) {
        val generatedInclude = layout.buildDirectory.file("dokka/includes/module.md")
        val prepareTask =
          tasks.register("prepareDokkaModuleDocs") { task ->
            task.inputs.file(readme)
            task.outputs.file(generatedInclude)
            task.doLast {
              val original = readme.readText()
              val sanitized =
                original.lines().joinToString("\n") { line ->
                  if (line.trimStart().startsWith("#")) {
                    line
                      .replace(Regex("(?i)\\bmodule\\b"), "Subsystem")
                      .replace(Regex("(?i)\\bpackage\\b"), "Namespace")
                  } else {
                    line
                  }
                }
              val content =
                buildString {
                  appendLine("# Module $modName")
                  appendLine()
                  append(sanitized)
                }
              val outputFile = generatedInclude.get().asFile
              outputFile.parentFile.mkdirs()
              outputFile.writeText(content)
            }
          }

        dokka.dokkaPublications.named("html") { publication ->
          publication.includes.from(generatedInclude)
        }
        dokka.dokkaSourceSets.configureEach { sourceSet ->
          sourceSet.includes.from(generatedInclude)
        }
        tasks.matching { it.name.startsWith("dokka") }.configureEach { task ->
          task.dependsOn(prepareTask)
        }
      }
    }
  }
}
