plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
    alias(libs.plugins.local.android.kmp.library)
    alias(libs.plugins.local.dependencies.koin)
    alias(libs.plugins.local.dependencies.navigation)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.features.trackerEdits.ui)
            implementation(projects.features.trackerEdits.domain)
            implementation(projects.features.trackerEdits.data)
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
