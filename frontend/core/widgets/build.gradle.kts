plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
    alias(libs.plugins.local.android.kmp.library)
    alias(libs.plugins.local.compose)
    alias(libs.plugins.local.dependencies.compose)
    alias(libs.plugins.local.dependencies.di)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.designSystem)
            api(projects.core.data)
            implementation(projects.core.logging)
            implementation(libs.bundles.coil)
        }
    }
}

dependencies {
    androidMainImplementation(libs.androidx.core.ktx)
}
