package org.vonvikken.growthbot.bot

import org.vonvikken.growthbot.bold
import org.vonvikken.growthbot.emoji

internal class BotMessage private constructor(title: String, textBlock: () -> String, emoji: String? = null) {

    enum class Type(val title: String, val emoji: String? = null) {
        APPLICATION("Growth Bot", "baby_bottle"),
        ERROR("Error", "exclamation"),
        SERVICE("Service message", "gear"),
        INFO("Information", "information_source"),
        HELP("Help", "question")
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
