package dev.fnc314.aijobtracker.gradletools.docs.tree

import org.jetbrains.dokka.CoreExtensions
import org.jetbrains.dokka.plugability.DokkaPlugin
import org.jetbrains.dokka.plugability.DokkaPluginApiPreview
import org.jetbrains.dokka.plugability.Extension
import org.jetbrains.dokka.plugability.PluginApiPreviewAcknowledgement

/**
 * Native documentation tree navigation plugin.
 *
 * Implements Dokka's pluggability contract to restructure multi-module
 * documentation into a hierarchical collapsible tree in `navigation.html` and `index.html`.
 */
public class NavigationTreePlugin : DokkaPlugin() {

  @OptIn(DokkaPluginApiPreview::class)
  override fun pluginApiPreviewAcknowledgement(): PluginApiPreviewAcknowledgement =
    PluginApiPreviewAcknowledgement

  public val treeTransformer: Extension<org.jetbrains.dokka.renderers.PostAction, *, *> by extending {
    CoreExtensions.postActions providing ::NavigationTreePostAction
  }
}
