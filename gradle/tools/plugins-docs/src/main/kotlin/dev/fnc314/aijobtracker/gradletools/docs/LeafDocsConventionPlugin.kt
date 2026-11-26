package dev.fnc314.aijobtracker.gradletools.docs

import org.gradle.api.Project
import org.jetbrains.dokka.gradle.DokkaExtension
import java.io.File
import java.net.URI

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

      // Configure third-party external documentation links for cross-referencing external APIs
      dokka.dokkaSourceSets.configureEach { sourceSet ->
        sourceSet.externalDocumentationLinks.register("clikt") { link ->
          link.url.set(URI.create("https://ajalt.github.io/clikt/api/"))
          link.packageListUrl.set(URI.create("https://ajalt.github.io/clikt/api/package-list"))
        }
        sourceSet.externalDocumentationLinks.register("mordant") { link ->
          link.url.set(URI.create("https://ajalt.github.io/mordant/api/"))
          link.packageListUrl.set(URI.create("https://ajalt.github.io/mordant/api/package-list"))
        }
        sourceSet.externalDocumentationLinks.register("kotlinx-coroutines") { link ->
          link.url.set(URI.create("https://kotlinlang.org/api/kotlinx.coroutines/"))
          link.packageListUrl.set(URI.create("https://kotlinlang.org/api/kotlinx.coroutines/package-list"))
        }
        sourceSet.externalDocumentationLinks.register("kotlinx-serialization") { link ->
          link.url.set(URI.create("https://kotlinlang.org/api/kotlinx.serialization/"))
          link.packageListUrl.set(URI.create("https://kotlinlang.org/api/kotlinx.serialization/package-list"))
        }
        sourceSet.externalDocumentationLinks.register("kotlinx-datetime") { link ->
          link.url.set(URI.create("https://kotlinlang.org/api/kotlinx-datetime/"))
          link.packageListUrl.set(URI.create("https://kotlinlang.org/api/kotlinx-datetime/package-list"))
        }
        sourceSet.externalDocumentationLinks.register("ktor") { link ->
          link.url.set(URI.create("https://api.ktor.io/"))
          link.packageListUrl.set(URI.create("https://api.ktor.io/package-list"))
        }
        sourceSet.externalDocumentationLinks.register("coil") { link ->
          link.url.set(URI.create("https://coil-kt.github.io/coil/api/"))
          link.packageListUrl.set(URI.create("https://coil-kt.github.io/coil/api/package-list"))
        }
        sourceSet.externalDocumentationLinks.register("androidx") { link ->
          link.url.set(URI.create("https://developer.android.com/reference/kotlin/androidx/"))
          link.packageListUrl.set(URI.create("https://developer.android.com/reference/kotlin/androidx/package-list"))
        }
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
