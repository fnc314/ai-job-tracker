plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
    alias(libs.plugins.local.android.kmp.library)
    alias(libs.plugins.local.dependencies.koin)
    alias(libs.plugins.local.dependencies.navigation)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.features.addJobApplication.ui)
            implementation(projects.features.addJobApplication.domain)
            implementation(projects.features.addJobApplication.data)
            implementation(projects.core.data)
            implementation(libs.koin.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            // implementation(libs.koin.test)
        }
    }
}
