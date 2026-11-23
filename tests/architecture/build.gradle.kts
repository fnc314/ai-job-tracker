plugins {
  alias(libs.plugins.kotlinJvm)
}

kotlin {
  jvmToolchain(libs.versions.jvm.toolchain.get().toInt())
}

dependencies {
  testImplementation(project(":common"))
  testImplementation(libs.konsist)
  testImplementation(libs.kotlin.test)
}

tasks.test {
  useJUnitPlatform()
}
