@file:Suppress("unused")

package org.vonvikken.growthbot

import com.vdurmont.emoji.EmojiManager
import com.vdurmont.emoji.EmojiParser

internal fun String.emoji(): String = EmojiParser.parseToUnicode(":$this:").let {
    if (EmojiManager.isEmoji(it)) it else ""
}

internal fun String.bold(): String = "<b>$this</b>"
internal fun String.italic(): String = "<i>$this</i>"
internal fun String.monospace(): String = "<code>$this</code>"
internal fun String.strikethrough(): String = "<s>$this</s>"
internal fun String.underline(): String = "<u>$this</u>"
internal fun String.spoiler(): String = "<tg-spoiler>$this</tg-spoiler>"
