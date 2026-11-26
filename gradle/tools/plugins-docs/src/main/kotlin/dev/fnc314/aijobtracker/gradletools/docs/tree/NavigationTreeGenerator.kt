package dev.fnc314.aijobtracker.gradletools.docs.tree

import org.jetbrains.dokka.DokkaConfiguration
import java.io.File

/**
 * Pure Kotlin generator that transforms Dokka navigation and index pages
 * into a collapsible clean architecture hierarchy.
 */
public object NavigationTreeGenerator {

  private sealed interface TreeNode {
    data class Folder(val children: MutableMap<String, TreeNode> = mutableMapOf()) : TreeNode
    data class Leaf(
      val module: DokkaConfiguration.DokkaModuleDescription,
      val relPath: String,
      val srcDir: File,
      val name: String,
      val fullPath: String,
    ) : TreeNode
  }

  public fun process(outputDir: File, modules: List<DokkaConfiguration.DokkaModuleDescription>) {
    val tree = mutableMapOf<String, TreeNode>()

    for (m in modules) {
      val srcDir = m.sourceOutputDirectory
      val srcPath = srcDir.absolutePath.replace("\\", "/")
      val relPath = m.relativePathToOutputDirectory.path.replace("\\", "/").trim('/')

      val subsystem = when {
        "/frontend/" in srcPath -> "frontend"
        "/backend/" in srcPath -> "backend"
        "/records/" in srcPath -> "records"
        "/internal/" in srcPath -> "internal"
        "/gradle/tools/" in srcPath -> "tools"
        else -> "other"
      }

      val fullPath = when {
        relPath.startsWith("$subsystem/") -> relPath
        subsystem != "other" -> "$subsystem/$relPath"
        else -> relPath
      }

      val tokens = fullPath.split("/").filter { it.isNotEmpty() }
      if (tokens.isEmpty()) continue

      var curr = tree
      for (t in tokens.dropLast(1)) {
        val folder = curr.getOrPut(t) { TreeNode.Folder() } as TreeNode.Folder
        curr = folder.children
      }

      val leaf = tokens.last()
      curr[leaf] = TreeNode.Leaf(
        module = m,
        relPath = relPath,
        srcDir = srcDir,
        name = leaf,
        fullPath = fullPath,
      )
    }

    // 1. Generate nested navigation.html
    val navLines = mutableListOf<String>()
    navLines.add("<div class=\"sideMenu\">")
    for (rootCat in listOf("frontend", "backend", "records", "tools", "internal")) {
      val node = tree[rootCat] ?: continue
      navLines.add(renderNavigationNode(rootCat, node, "", 0))
    }
    navLines.add("</div>")

    val fullNavHtml = navLines.joinToString("\n")
    val rootNavFile = File(outputDir, "navigation.html")
    rootNavFile.writeText(fullNavHtml)

    // 2. Update navigation.html in each module output directory with depth prefix
    for (m in modules) {
      val relPath = m.relativePathToOutputDirectory.path.replace("\\", "/").trim('/')
      val modNavFile = File(outputDir, "$relPath/navigation.html")
      val parent = modNavFile.parentFile
      if (parent != null && parent.exists()) {
        val depth = relPath.split("/").filter { it.isNotEmpty() }.size
        val prefix = "../".repeat(depth)

        val modNavHtml = fullNavHtml.replace(Regex("""href="([^"]+)"""")) { match ->
          val href = match.groupValues[1]
          if (href.startsWith("http") || href.startsWith("#") || href.startsWith("/")) {
            match.value
          } else {
            "href=\"$prefix$href\""
          }
        }
        modNavFile.writeText(modNavHtml)
      }
    }

    // 3. Update main module table in index.html
    val indexFile = File(outputDir, "index.html")
    if (indexFile.exists()) {
      val originalIdx = indexFile.readText()
      val descriptions = mutableMapOf<String, String>()

      val rowRegex = Regex("""<div class="table-row table-row_multimodule">.*?</div>\s*</div>\s*</div>""", RegexOption.DOT_MATCHES_ALL)
      val linkRegex = Regex("""<a href="([^/"]+)/index\.html">([^<]+)</a>""")
      val descRegex = Regex("""<span class="brief-comment">(.*?)</span>""", RegexOption.DOT_MATCHES_ALL)

      for (r in rowRegex.findAll(originalIdx)) {
        val matchLink = linkRegex.find(r.value)
        val matchDesc = descRegex.find(r.value)
        if (matchLink != null) {
          val target = matchLink.groupValues[1]
          val desc = matchDesc?.groupValues?.get(1)?.trim() ?: ""
          descriptions[target] = desc
        }
      }

      val tableRows = mutableListOf<String>()

      fun renderIndexRows(name: String, node: TreeNode, level: Int) {
        val indentRem = level * 1.5
        when (node) {
          is TreeNode.Folder -> {
            tableRows.add(
              """      <div class="table-row table-row_multimodule">
        <div class="main-subrow ">
          <div class="w-100" style="padding-left: ${indentRem}rem; font-weight: 600;">
            <span class="inline-flex">
              <div>📁 <strong>${escapeHtml(name)}</strong></div>
            </span>
          </div>
          <div><span class="brief-comment"></span></div>
        </div>
      </div>"""
            )
            for ((childName, childNode) in node.children.toSortedMap()) {
              renderIndexRows(childName, childNode, level + 1)
            }
          }
          is TreeNode.Leaf -> {
            val relPath = node.relPath
            val leafName = node.name
            val desc = descriptions[relPath] ?: ""
            tableRows.add(
              """      <div class="table-row table-row_multimodule">
        <div class="main-subrow ">
          <div class="w-100" style="padding-left: ${indentRem}rem;">
            <span class="inline-flex">
              <div><a href="$relPath/index.html">📦 ${escapeHtml(leafName)}</a></div>
            </span>
          </div>
          <div><span class="brief-comment">$desc</span></div>
        </div>
      </div>"""
            )
          }
        }
      }

      for (rootCat in listOf("frontend", "backend", "records", "tools", "internal")) {
        val node = tree[rootCat] ?: continue
        renderIndexRows(rootCat, node, 0)
      }

      val newTableHtml = "<div class=\"table\">\n" + tableRows.joinToString("\n\n") + "\n    </div>"
      val tableReplaceRegex = Regex("""<div class="table">.*?</div>\s*</div>\s*</div>\s*<div class="footer">""", RegexOption.DOT_MATCHES_ALL)
      val updatedIdx = tableReplaceRegex.replace(originalIdx, "$newTableHtml\n  </div>\n</div>\n<div class=\"footer\">")
      indexFile.writeText(updatedIdx)
    }
  }

  private fun sanitizeId(s: String): String =
    s.replace(Regex("""[^a-zA-Z0-9_-]"""), "_")

  private fun escapeHtml(s: String): String =
    s.replace("&", "&amp;")
      .replace("<", "&lt;")
      .replace(">", "&gt;")
      .replace("\"", "&quot;")

  private fun renderNavigationNode(name: String, node: TreeNode, pathPrefix: String, level: Int): String {
    val currentPath = if (pathPrefix.isNotEmpty()) "$pathPrefix/$name" else name
    val navId = sanitizeId("$currentPath-nav-submenu")

    return when (node) {
      is TreeNode.Folder -> {
        val out = mutableListOf<String>()
        out.add("<div class=\"toc--part\" id=\"$navId\" data-nesting-level=\"$level\">")
        out.add("  <div class=\"toc--row\">")
        out.add("    <button class=\"toc--button\" aria-expanded=\"false\" aria-label=\"${escapeHtml(name)}\" onclick=\"window.handleTocButtonClick(event, '$navId')\"></button>")
        out.add("    <a href=\"#\" class=\"toc--link\" onclick=\"window.handleTocButtonClick(event, '$navId'); return false;\"><span><span>${escapeHtml(name)}</span></span></a>")
        out.add("  </div>")
        for ((childName, childNode) in node.children.toSortedMap()) {
          out.add(renderNavigationNode(childName, childNode, currentPath, level + 1))
        }
        out.add("</div>")
        out.joinToString("\n")
      }
      is TreeNode.Leaf -> {
        val relPath = node.relPath
        val srcNav = File(node.srcDir, "navigation.html")
        if (!srcNav.exists()) {
          "<div class=\"toc--part\" id=\"$navId\" data-nesting-level=\"$level\">" +
            "<div class=\"toc--row\"><a href=\"$relPath/index.html\" class=\"toc--link\">" +
            "<span><span>${escapeHtml(name)}</span></span></a></div></div>"
        } else {
          var snippet = srcNav.readText()
          snippet = snippet.replace(Regex("""^\s*<dokka-template-command[^>]*>\s*"""), "")
          snippet = snippet.replace(Regex("""\s*</dokka-template-command>\s*$"""), "")

          snippet = snippet.replace(Regex("""href="([^"]+)"""")) { match ->
            val href = match.groupValues[1]
            if (href.startsWith("http") || href.startsWith("#") || href.startsWith("/")) {
              match.value
            } else {
              "href=\"$relPath/$href\""
            }
          }

          snippet = snippet.replace("class=\"toc--skip-link\" href=\"\"", "class=\"toc--skip-link\" href=\"$relPath/\"")

          snippet = snippet.replace(Regex("""data-nesting-level="(\d+)"""")) { match ->
            val oldLvl = match.groupValues[1].toInt()
            "data-nesting-level=\"${level + oldLvl}\""
          }

          val topMatch = Regex("""<div class="toc--part" id="([^"]+)"""").find(snippet)
          if (topMatch != null) {
            val oldTopId = topMatch.groupValues[1]
            snippet = snippet.replaceFirst("id=\"$oldTopId\"", "id=\"$navId\"")
            snippet = snippet.replace("'$oldTopId'", "'$navId'")
            snippet = snippet.replace(Regex("""id="${Regex.escape(oldTopId)}-(\d+)""""), "id=\"$navId-$1\"")
            snippet = snippet.replace(Regex("""'${Regex.escape(oldTopId)}-(\d+)'"""), "'$navId-$1'")
            snippet = snippet.replace("'$oldTopId-", "'$navId-")
          }

          snippet
        }
      }
    }
  }
}
