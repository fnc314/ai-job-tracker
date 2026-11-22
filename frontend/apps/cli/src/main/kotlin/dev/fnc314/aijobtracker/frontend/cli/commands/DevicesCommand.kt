package dev.fnc314.aijobtracker.frontend.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.TextColors.cyan
import com.github.ajalt.mordant.rendering.TextColors.gray
import com.github.ajalt.mordant.rendering.TextColors.green
import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import dev.fnc314.aijobtracker.frontend.cli.db.RemoteDataSourceProvider
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Top-level Clikt command managing registered client devices (`devices list`, `devices purge`).
 */
public class DevicesCommand : CliktCommand(name = "devices") {
    init {
        subcommands(
            ListDevicesCommand(),
            PurgeDevicesCommand(),
        )
    }

    override fun run(): Unit = Unit
}

internal class ListDevicesCommand : CliktCommand(name = "list") {
    private val jsonOutput: Boolean by option("--json", help = "Output as JSON").flag(default = false)

    override fun run() {
        val t = Terminal(nonInteractiveWidth = 200)
        val db = RemoteDataSourceProvider.remote
        val installationDocs = db.collection("installations").get().get().documents

        val allDevices = mutableListOf<Map<String, Any?>>()
        for (doc in installationDocs) {
            val data = doc.data.toMutableMap()
            data["installationId"] = doc.id
            allDevices.add(data)
        }

        if (jsonOutput) {
            val jsonList = allDevices.map { doc ->
                JsonObject(doc.mapValues { (_, v) -> JsonPrimitive(v?.toString()) })
            }
            t.println(Json.encodeToString(jsonList))
            return
        }

        if (allDevices.isEmpty()) {
            t.println(gray("No installations registered."))
            return
        }

        t.println(
            table {
                header {
                    row("Installation ID", "Platform", "Status", "Updated At", "Token")
                }
                body {
                    allDevices.forEach { dev ->
                        val instId = (dev["installationId"] as? String)?.take(24) ?: "-"
                        val plat = (dev["platform"] as? String) ?: "-"
                        val status = (dev["status"] as? String) ?: "-"
                        val updated = (dev["updatedAt"] ?: dev["createdAt"])?.toString()?.take(19) ?: "-"
                        val tok = (dev["token"] as? String)?.take(16)?.plus("...") ?: "-"
                        row(cyan(instId), plat, status, updated, gray(tok))
                    }
                }
            }
        )
    }
}

internal class PurgeDevicesCommand : CliktCommand(name = "purge") {
    private val force: Boolean by option("--force", "-f", help = "Bypass confirmation prompt").flag(default = false)

    override fun run() {
        val t = Terminal(nonInteractiveWidth = 200)
        val allowWrites = System.getenv("TRACKER_ALLOW_WRITES") == "1"
        if (!allowWrites) {
            t.println(red("Error: Writes are disabled. Set TRACKER_ALLOW_WRITES=1 to purge devices."))
            return
        }

        val db = RemoteDataSourceProvider.remote
        val installationDocs = db.collection("installations").get().get().documents
        var purgedCount = 0

        for (doc in installationDocs) {
            doc.reference.delete().get()
            purgedCount++
        }

        t.println(green("✓ Successfully purged $purgedCount registered installation(s)."))
    }
}
