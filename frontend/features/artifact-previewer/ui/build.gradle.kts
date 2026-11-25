plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
    alias(libs.plugins.local.android.kmp.library)
    alias(libs.plugins.local.compose)
    alias(libs.plugins.local.dependencies.compose)
    alias(libs.plugins.local.dependencies.di)
    alias(libs.plugins.local.dependencies.navigation)
    alias(libs.plugins.local.dependencies.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.documents)
            implementation(projects.core.navigation)
            implementation(projects.core.widgets)
            implementation(projects.features.artifactPreviewer.domain)
            implementation(libs.bundles.androidx.lifecycle.compose)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.markdown.renderer.m3)
        }
    }
}
