plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
    alias(libs.plugins.local.android.kmp.library)
    alias(libs.plugins.local.dependencies.di)
    alias(libs.plugins.local.dependencies.datasource)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.companyDetails.domain)
            implementation(projects.core.data)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
