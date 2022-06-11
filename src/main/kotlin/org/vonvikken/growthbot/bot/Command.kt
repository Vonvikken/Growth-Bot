package org.vonvikken.growthbot.bot

import org.vonvikken.growthbot.Try
import org.vonvikken.growthbot.db.Gender
import org.vonvikken.growthbot.manage
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")

internal typealias CommandCallback = (bot: GrowthBot, args: List<String>) -> Unit

internal sealed class Command(
    val commandName: String,
    val params: String = "",
    val description: String,
    val longDescription: String = "",
    val callback: CommandCallback
)

internal object NewBaby : Command(
    "new",
    "baby-name {m|f} [date-of-birth]",
    "Add a new baby.",
    """Add a new baby given his/her name, gender and optionally date of birth. The new baby will be automatically set as the current one.
        |If the date of birth is not specified, the default is the current day.
    """.trimMargin(),
    { bot, args ->
        checkEmptyArguments(
            args,
            {
                // TODO check correct command syntax
                val name = args[0]
                val gender = if (args[1] == "m") Gender.MALE else Gender.FEMALE
                val birthDate = args.elementAtOrNull(2)?.toDate() ?: LocalDate.now()
                bot.addBaby(name, gender, birthDate)
            }
        ) {
            bot.sendErrorMessage { "Wrong parameters! See help for the correct syntax!" }
        }
    }
)

internal object SwitchBaby : Command(
    "switch",
    "baby-name",
    "Switch to another existing baby.",
    "Switch the current baby to another already existing one.",
    { bot, args ->
        checkEmptyArguments(
            args,
            { bot.sendInfoMessage { "Switched to ${args.joinToString(separator = " ")}!" } },
            { bot.sendErrorMessage { "Specify a name as argument." } },
        )
    }
)

internal object DeleteBaby : Command(
    "delete",
    "baby-name",
    "Delete an existing baby.",
    "Delete the specified baby from the database.",
    { bot, args ->
        checkEmptyArguments(
            args,
            { bot.sendInfoMessage { "${args.joinToString(separator = " ")} deleted!" } },
            { bot.sendErrorMessage { "Specify a name as argument." } },
        )
    }
)

internal object Weight : Command(
    "weight",
    "weight [date]",
    "Add a weight measurement.",
    """Add a weight measurement in grams for the current baby.
        |If the date is not specified, the default is the current day.
    """.trimMargin(),
    { bot, args ->
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
    }
)

internal object Length : Command(
    "length",
    "length [date]",
    "Add a length measurement.",
    """Add a length measurement in centimeters for the current baby.
        |If the date is not specified, the default is the current day.
    """.trimMargin(),
    { bot, args ->
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
    }
)

private fun checkEmptyArguments(args: List<String>, ifOk: () -> Unit, ifError: () -> Unit) =
    if (args.isNotEmpty()) ifOk() else ifError()

// TODO check format
private fun String.toDate(): LocalDate = LocalDate.parse(this, DATE_FORMAT)

private fun LocalDate.toDateString(): String = this.format(DATE_FORMAT)
