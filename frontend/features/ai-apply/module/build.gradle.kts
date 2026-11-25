plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
    alias(libs.plugins.local.android.kmp.library)
    alias(libs.plugins.local.dependencies.koin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.features.aiApply.ui)
            implementation(projects.features.aiApply.domain)
            implementation(projects.features.aiApply.data)
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
