package org.vonvikken.growthbot.bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.logging.LogLevel
import org.vonvikken.growthbot.Config
import org.vonvikken.growthbot.HashCalc
import org.vonvikken.growthbot.bold
import org.vonvikken.growthbot.emoji
import org.vonvikken.growthbot.italic
import org.vonvikken.growthbot.logger
import org.vonvikken.growthbot.monospace

internal class GrowthBot(config: Config, vararg commands: Command) {

    private val log by logger {}
    private val chatID: ChatId.Id = ChatId.Id(config.chatID)
    private val botToken: String = config.token

    private val bot: Bot
    val chatIDHash: String = HashCalc.sha3256(config.chatID)
    var currentBabyID: Int = -1

    init {
        bot = bot {
            token = botToken
            logLevel = LogLevel.Error
            dispatch {
                commands.forEach { installCommand(it) }
                installHelpForCommands(commands.asList())
            }
        }.apply(Bot::startPolling)

        sendServiceMessage {
            val check = "white_check_mark".emoji()
            val rocket = "rocket".emoji()

            """$check ${"Bot started!".italic().bold()} $rocket
              |
              |<i>Use <code>/help</code> to list all the available commands</i>
            """.trimMargin()
        }
        log.info("Growth bot started.")
    }

    internal fun sendApplicationMessage(textBlock: () -> String) =
        sendTypedMessage(BotMessage.Type.APPLICATION, textBlock)

    internal fun sendErrorMessage(textBlock: () -> String) = sendTypedMessage(BotMessage.Type.ERROR, textBlock)

    internal fun sendInfoMessage(textBlock: () -> String) = sendTypedMessage(BotMessage.Type.INFO, textBlock)

    private fun sendServiceMessage(textBlock: () -> String) = sendTypedMessage(BotMessage.Type.SERVICE, textBlock)

    private fun sendTypedMessage(type: BotMessage.Type, textBlock: () -> String) =
        sendMessage(BotMessage.createMessage(type, textBlock))

    private fun sendMessage(message: BotMessage) {
        val result = bot.sendMessage(chatID, message.text, ParseMode.HTML)
        log.debug("Sent message: ${message.text}")
        result.fold({}, { log.error("Error! $it") })
    }

    internal fun sendStopMessage() {
        sendServiceMessage {
            "${"stop_sign".emoji()} ${"Bot stopped!".italic().bold()} ${"hand".emoji()}"
        }
    }

    private fun Message?.ifAuthorized(block: () -> Unit) {
        if (this?.chat?.id == chatID.id) block()
    }

    private fun Dispatcher.installCommand(cmd: Command) {
        command(cmd.commandName) {
            update.message.ifAuthorized { cmd.callback(this@GrowthBot, args) }
        }
    }

    private fun Dispatcher.installHelpForCommands(commands: List<Command>) {
        command("help") {
            update.message.ifAuthorized {
                if (args.isEmpty()) {
                    genericHelp(commands)
                } else {
                    commands.firstOrNull { it.commandName == args[0] }?.help()
                }?.let {
                    sendMessage(BotMessage.createMessage(BotMessage.Type.HELP) { it })
                }
            }
        }
    }

    private fun genericHelp(commands: List<Command>): String {
        val helpParam = "command-name".monospace()
        return StringBuilder().apply {
            appendLine("Available commands".bold())
            appendLine("\u2022 /help \u2192 Print a list of the available commands.")
            appendLine("\u2022 /help $helpParam \u2192 Print a detailed help of $helpParam.")
            commands.forEach {
                val cmdLine = listOf(it.commandName, it.params.monospace()).joinToString(" ")
                appendLine("\u2022 /$cmdLine \u2192 ${it.description}")
            }
        }.toString()
    }

    private fun Command.help(): String {
        return """Help for command ${commandName.bold()}
            |
            |Parameters: ${params.monospace()}
            |
            |$longDescription
        """.trimMargin()
    }
}
