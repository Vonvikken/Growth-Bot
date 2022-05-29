package org.vonvikken.growthbot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.network.fold

private typealias OptionalCallback = (() -> Unit)?

internal class GrowthBot(config: Config, vararg commands: Pair<Command, () -> Unit>) {

    private val log by logger {}
    private val bot: Bot
    private val chatID: Long = config.chatID
    private val botToken: String = config.token

    init {
        bot = bot {
            token = botToken
            logLevel = LogLevel.Error
            dispatch {
                commands.forEach { installCommand(it.first, it.second) }

                installCommand(Command.HELP) {
                    sendMessage(
                        BotMessage.createMessage(BotMessage.Type.HELP) {
                            return@createMessage StringBuilder().apply {
                                Command.values().forEach { cmd ->
                                    append("\u2022 /${cmd.commandName} ")
                                    appendLine("\u2192 ${cmd.description}")
                                }
                            }.toString()
                        }
                    )
                }
            }
        }.apply { startPolling() }

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

    private fun Dispatcher.installCommand(command: Command, block: OptionalCallback) {
        command(command.commandName) {
            block?.takeIf { checkMessageChatId(update.message) }?.invoke()
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
