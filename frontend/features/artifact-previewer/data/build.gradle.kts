plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
    alias(libs.plugins.local.android.kmp.library)
    alias(libs.plugins.local.dependencies.di)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.artifactPreviewer.domain)
            implementation(projects.core.documents)
            implementation(projects.core.data)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
