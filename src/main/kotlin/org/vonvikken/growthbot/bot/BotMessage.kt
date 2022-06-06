package org.vonvikken.growthbot.bot

import org.vonvikken.growthbot.bold
import org.vonvikken.growthbot.emoji

internal class BotMessage private constructor(title: String, textBlock: () -> String, emoji: String? = null) {

    enum class Type(val title: String, val emoji: String? = null) {
        APPLICATION("Growth Bot", "bell"),
        SERVICE("Service message", "gear"),
        NOTIFICATION("Notification received", "incoming_envelope"),
        HELP("Available commands", "information_source")
    }

    companion object {
        internal fun createMessage(messageType: Type, textBlock: () -> String): BotMessage =
            BotMessage(messageType.title, textBlock, messageType.emoji)
    }

    val text = """${emoji?.emoji() ?: ""} ${title.bold()}
                 |
                 |${textBlock()}
    """.trimMargin()
}
