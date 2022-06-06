package org.vonvikken.growthbot

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.nio.file.Path
import java.nio.file.Paths

private const val CONFIG_DEFAULT_PATH = "config.json"

fun main(args: Array<String>) = GrowthBotMain().main(args)

class GrowthBotMain : CliktCommand() {

    private val log by logger {}

    private val configPath by option(
        "-c",
        "--config",
        help = "path to the config file (default: \"./config.json\")"
    ).default(CONFIG_DEFAULT_PATH)

    override fun run() {
        val config = Config.fromFile(Paths.get(configPath))
        val bot = GrowthBot(config = config, NewBaby, SwitchBaby, DeleteBaby, Weight, Length)

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                bot.sendStopMessage()
                log.info("Growth bot stopped.")
            }
        })
    }
}

@Serializable
internal data class Config(
    val token: String,
    @SerialName("chat_id") val chatID: Long,
) {
    companion object {
        internal fun fromFile(path: Path): Config = Json.decodeFromString(path.toFile().readText())
    }
}
