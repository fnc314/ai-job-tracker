plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
    alias(libs.plugins.local.android.kmp.library)
    alias(libs.plugins.local.dependencies.di)
    alias(libs.plugins.local.dependencies.datasource)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.documents)
            implementation(projects.core.data)
            implementation(projects.features.jobApplicationDetails.domain)
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
