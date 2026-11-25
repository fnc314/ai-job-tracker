plugins {
  alias(tools.plugins.docs.root)
}

// Root build file for ai-job-tracker composite monorepo

dependencies {
  // Records Subsystem
  dokka(local.bundles.records)

  // Tools & Project Plugins
  dokka(local.bundles.tools)

  // Backend Subsystem
  dokka(local.bundles.backend)

  // Frontend Subsystem
  dokka(local.bundles.frontend)

  // Internal Subsystem
  dokka(local.bundles.internal)
}

tasks.register("buildAll") {
  group = "build"
  description = "Assembles all included builds across records, backend, and frontend"
  dependsOn(
    gradle.includedBuild("records").task(":proto:jar"),
    gradle.includedBuild("records").task(":parser:jar"),
    gradle.includedBuild("backend").task(":jar"),
    gradle.includedBuild("frontend").task(":apps-cli:installDist"),
    gradle.includedBuild("frontend").task(":apps-terminal:installDist"),
  )
}
