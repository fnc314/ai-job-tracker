plugins {
  alias(libs.plugins.kotlinJvm)
}

kotlin {
  jvmToolchain(libs.versions.jvm.toolchain.get().toInt())
}

dependencies {
  api(libs.konsist)
  api(libs.kotlin.test)
}
