import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
  alias(libs.plugins.kotlinJvm)
  alias(tools.plugins.docs.leaf)
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
  gradleApi()
}

gradlePlugin {
  plugins {
    create("settings-subsystem") {
      id = "dev.fnc314.aijobtracker.gradletools.settings.subsystem"
      description = "Settings convention plugin orchestrating workspace root discovery, catalog loading, and tool build inclusion"
      implementationClass = "dev.fnc314.aijobtracker.gradletools.settings.SubsystemSettingsPlugin"
      displayName = "Subsystem Settings Convention"
    }
  }
}
