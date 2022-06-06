package org.vonvikken.growthbot.bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.network.fold
import org.vonvikken.growthbot.Config
import org.vonvikken.growthbot.HashCalc
import org.vonvikken.growthbot.bold
import org.vonvikken.growthbot.emoji
import org.vonvikken.growthbot.italic
import org.vonvikken.growthbot.logger

internal class GrowthBot(config: Config, vararg commands: Command) {

    private val log by logger {}
    private val bot: Bot
    private val chatID: Long = config.chatID
    private val botToken: String = config.token

    val chatIDHash: String = HashCalc.sha3256(chatID)

    init {
        bot = bot {
            token = botToken
            logLevel = LogLevel.Error
            dispatch {
                commands.forEach { installCommand(it) }
                installHelpCommand(commands.map { it.commandName to it.description }.toList())
            }
        }.apply(Bot::startPolling)

        sendApplicationMessage {
            val check = "white_check_mark".emoji()
            val rocket = "rocket".emoji()

            """$check ${"Bot started!".italic().bold()} $rocket
              |
              |<i>Use <code>/help</code> to list all the available commands</i>
            """.trimMargin()
        }
        log.info("Growth bot started.")
    }

    internal fun sendApplicationMessage(textBlock: () -> String) {
        sendMessage(BotMessage.createMessage(BotMessage.Type.APPLICATION, textBlock))
    }

    private fun sendMessage(message: BotMessage) {
        val result = bot.sendMessage(chatID, message.text, ParseMode.HTML)
        log.debug("Sent message: ${message.text}")
        result.fold({}, { log.error("Error! ${it.errorBody}") })
    }

    internal fun sendStopMessage() {
        sendApplicationMessage {
            "${"stop_sign".emoji()} ${"Bot stopped!".italic().bold()} ${"hand".emoji()}"
        }
    }

    private fun Dispatcher.installHelpCommand(cmds: List<Pair<String, String>>) {
        command("help") {
            if (checkMessageChatId(update.message)) {
                sendMessage(
                    BotMessage.createMessage(BotMessage.Type.HELP) {
                        return@createMessage StringBuilder().apply {
                            appendLine("\u2022 /help \u2192 Print a list of the available commands.")
                            cmds.forEach { cmd -> appendLine("\u2022 /${cmd.first} \u2192 ${cmd.second}") }
                        }.toString()
                    }
                )
            }
        }
    }

    private fun Dispatcher.installCommand(cmd: Command) {
        command(cmd.commandName) {
            cmd.callback.takeIf { checkMessageChatId(update.message) }?.invoke()
        }
    }

    private fun checkMessageChatId(message: Message?): Boolean {
        if (message == null) return false

        val username = message.from?.username ?: "[Unknown username]"
        val text = message.text ?: "[No text]"
        val chatId = message.chat.id

        val isOk = chatID == chatId

        if (isOk) {
            log.debug("Message received from user $username:\n\t$text")
        } else {
            log.warn("Message received from unauthorized user $username:\n\t$text")
        }

        return isOk
    }
}
