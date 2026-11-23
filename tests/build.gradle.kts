// Root project for tests subsystem aggregating all test subprojects

tasks.register("test") {
  group = "verification"
  description = "Runs all test suites across tests subprojects"
  dependsOn(gradle.includedBuild("tests").task(":architecture:test"))
  dependsOn(gradle.includedBuild("tests").task(":standards:test"))
}
