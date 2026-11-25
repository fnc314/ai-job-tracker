import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
  alias(libs.plugins.kotlinJvm)
  `java-gradle-plugin`
}

group = "dev.fnc314.aijobtracker.tools"
version = local.versions.version.get()

kotlin {
  @OptIn(ExperimentalAbiValidation::class)
  abiValidation {
    referenceDumpDir = layout.projectDirectory.dir("api")
  }
  explicitApi()
  jvmToolchain(
    libs.versions.jvm.toolchain.get().toInt()
  )
  compilerOptions {
    apiVersion = libs.versions.kotlin.asProvider().map {
      KotlinVersion.fromVersion(it.substringBeforeLast("."))
    }
    languageVersion = libs.versions.kotlin.asProvider().map {
      KotlinVersion.fromVersion(it.substringBeforeLast("."))
    }
  }
}

dependencies {
  compileOnly(gradleApi())
  api(libs.dokka.gradle.plugin)
}

gradlePlugin {
  plugins {
    create("docs-root") {
      id = "dev.fnc314.aijobtracker.gradletools.docs.root"
      description = "Dokka Root Convention Plugin - Configures aggregated repository-wide documentation"
      implementationClass = "dev.fnc314.aijobtracker.gradletools.docs.RootDocsConventionPlugin"
      displayName = "Docs Root Convention"
    }
    create("docs-node") {
      id = "dev.fnc314.aijobtracker.gradletools.docs.node"
      description = "Dokka Node Convention Plugin - Aggregates child leaf and node documentation bundles"
      implementationClass = "dev.fnc314.aijobtracker.gradletools.docs.NodeDocsConventionPlugin"
      displayName = "Docs Node Convention"
    }
    create("docs-leaf") {
      id = "dev.fnc314.aijobtracker.gradletools.docs.leaf"
      description = "Dokka Leaf Convention Plugin - Generates documentation and binds API links for leaf projects"
      implementationClass = "dev.fnc314.aijobtracker.gradletools.docs.LeafDocsConventionPlugin"
      displayName = "Docs Leaf Convention"
    }
  }
}
