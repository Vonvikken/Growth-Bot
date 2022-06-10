package org.vonvikken.growthbot.bot

import org.vonvikken.growthbot.Try
import org.vonvikken.growthbot.manage
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")

internal typealias CommandCallback = (bot: GrowthBot, args: List<String>) -> Unit

internal sealed class Command(val commandName: String, val description: String, val callback: CommandCallback)

internal object NewBaby : Command("new", "Add a new baby.", { bot, args ->
    checkEmptyArguments(
        args,
        { bot.sendInfoMessage { "${args.joinToString(separator = " ")} added!" } },
        { bot.sendErrorMessage { "Specify a name as argument." } }
    )
})

internal object SwitchBaby : Command("switch", "Switch to another existing baby.", { bot, args ->
    checkEmptyArguments(
        args,
        { bot.sendInfoMessage { "Switched to ${args.joinToString(separator = " ")}!" } },
        { bot.sendErrorMessage { "Specify a name as argument." } },
    )
})

internal object DeleteBaby : Command("delete", "Delete an existing baby.", { bot, args ->
    checkEmptyArguments(
        args,
        { bot.sendInfoMessage { "${args.joinToString(separator = " ")} deleted!" } },
        { bot.sendErrorMessage { "Specify a name as argument." } },
    )
})

internal object Weight : Command("weight", "Add a weight measurement.", { bot, args ->
    checkEmptyArguments(args, {
        Try {
            val weight = args[0].toInt()
            val date = args.elementAtOrNull(1)?.toDate() ?: LocalDate.now()
            "Weight: $weight g Date: ${date.toDateString()}"
        }.manage({
            bot.sendApplicationMessage { it }
        }, {
            bot.sendErrorMessage { it.error.message!! }
        })
    }, { bot.sendErrorMessage { "Insert at least the weight measurement in grams." } })
})

internal object Length : Command("length", "Add a length measurement.", { bot, args ->
    checkEmptyArguments(args, {
        Try {
            val weight = args[0].toInt()
            val date = args.elementAtOrNull(1)?.toDate() ?: LocalDate.now()
            "Weight: $weight cm Date: ${date.toDateString()}"
        }.manage({
            bot.sendApplicationMessage { it }
        }, {
            bot.sendErrorMessage { it.error.message!! }
        })
    }, { bot.sendErrorMessage { "Insert at least the length measurement in centimeters." } })
})

private fun checkEmptyArguments(args: List<String>, ifOk: () -> Unit, ifError: () -> Unit) =
    if (args.isNotEmpty()) ifOk() else ifError()

// TODO check format
private fun String.toDate(): LocalDate = LocalDate.parse(this, DATE_FORMAT)

private fun LocalDate.toDateString(): String = this.format(DATE_FORMAT)
