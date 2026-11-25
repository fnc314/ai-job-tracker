package dev.fnc314.aijobtracker.gradletools.settings

import org.gradle.api.Action
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import java.io.File
import javax.inject.Inject

public open class SubsystemSettingsExtension @Inject constructor(
  objects: ObjectFactory,
) {
  /**
   * Root directory of the repository/workspace.
   *
   * If unset, auto-resolved via:
   * 1. GRADLE_DOCS_ROOT env var
   * 2. WORKSPACE_ROOT env var
   * 3. Upward filesystem search for gradle/wrapper/gradle-wrapper.properties
   */
  public val projectRoot: DirectoryProperty = objects.directoryProperty()

  internal val catalogsList = mutableMapOf<String, String?>()
  internal val toolBuildsList = mutableListOf<String>()

  public fun catalogs(action: Action<CatalogsDsl>) {
    val dsl = CatalogsDsl(catalogsList)
    action.execute(dsl)
  }

  public fun toolBuilds(action: Action<ToolBuildsDsl>) {
    val dsl = ToolBuildsDsl(toolBuildsList)
    action.execute(dsl)
  }

  public class CatalogsDsl(private val map: MutableMap<String, String?>) {
    /**
     * Registers a catalog name.
     * Abbreviated syntax: register("libs") implicitly maps to "gradle/libs.versions.toml".
     */
    public fun register(name: String) {
      map[name] = null
    }

    /**
     * Registers a catalog name with a custom relative path.
     */
    public fun register(name: String, path: String) {
      map[name] = path
    }
  }

  public class ToolBuildsDsl(private val list: MutableList<String>) {
    public fun include(relativePath: String) {
      list.add(relativePath)
    }
  }
}
