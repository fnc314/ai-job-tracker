plugins {
  alias(libs.plugins.local.kotlin.multiplatform)
  alias(libs.plugins.local.android.kmp.library)
  alias(libs.plugins.local.compose)
  alias(libs.plugins.local.dependencies.compose)
  alias(libs.plugins.local.dependencies.serialization)
  alias(libs.plugins.local.dependencies.di)
  alias(libs.plugins.local.dependencies.navigation)
}

kotlin {
  js {
    binaries.executable()
  }
  @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
  wasmJs {
    binaries.executable()
  }
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.logging)
      implementation(projects.core.auth)
      implementation(projects.core.documents)
      implementation(projects.core.data)
      implementation(projects.core.network)
      implementation(projects.core.navigation)
      implementation(projects.core.designSystem)
      implementation(projects.core.widgets)

      // Feature Assembly Modules
      implementation(projects.features.signIn.module)
      implementation(projects.features.jobApplicationsList.module)
      implementation(projects.features.jobApplicationDetails.module)
      implementation(projects.features.companiesList.module)
      implementation(projects.features.companyDetails.module)
      implementation(projects.features.settings.module)
      implementation(projects.features.addJobApplication.module)
      implementation(projects.features.aiApply.module)
      implementation(projects.features.documentsViewer.module)
      implementation(projects.features.trackerEdits.module)

      // Lifecycle
      implementation(libs.bundles.androidx.lifecycle.compose)
    }
    commonTest.dependencies {
      implementation(libs.kotlin.test)
    }
    iosMain.dependencies {
      implementation(libs.kmp.inspector)
    }
    jvmMain.dependencies {
      implementation(libs.kmp.inspector)
    }
    jsMain.dependencies {
      implementation(libs.wrappers.browser)
    }
  }
}

dependencies {
  androidMainImplementation(libs.kmp.inspector)
}
