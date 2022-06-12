package org.vonvikken.growthbot.bot

import org.vonvikken.growthbot.bold
import org.vonvikken.growthbot.db.DataOperations
import org.vonvikken.growthbot.db.Gender
import org.vonvikken.growthbot.italic
import org.vonvikken.growthbot.logger
import org.vonvikken.growthbot.manage
import org.vonvikken.growthbot.measure.elapsedDays
import org.vonvikken.growthbot.toDateString
import org.vonvikken.growthbot.underline
import java.time.LocalDate

private val log by logger { }

internal fun GrowthBot.addBaby(name: String, gender: Gender, birthDate: LocalDate) {
    DataOperations.addBaby(name, gender, birthDate, chatIDHash).manage({ babyID ->
        currentBabyID = babyID.value
        sendInfoMessage { "${name.bold()} added!" }
    }, {
        sendErrorMessage { "Error! Cannot add ${name.italic()}!" }
        log.error(it.error.message)
    })
}

internal fun GrowthBot.switchBaby(name: String) {
    DataOperations.switchToBaby(name).manage({ babyID ->
        currentBabyID = babyID
        sendInfoMessage { "Current baby is ${name.bold()}!" }
        log.info("Current Baby ID: $currentBabyID")
    }, {
        sendErrorMessage { "Error! Cannot switch to ${name.italic()}!" }
        log.error(it.error.message)
    })
}

internal fun GrowthBot.deleteBaby(name: String) {
    DataOperations.deleteBaby(name).manage({
        if (it > 0) sendInfoMessage { "${name.bold()} removed!" } else sendInfoMessage { "${name.italic()} not found!" }
    }, {
        sendErrorMessage { "Error! Cannot remove ${name.italic()}!" }
        log.error(it.error.message)
    })
}

internal fun GrowthBot.addWeight(value: Int, date: LocalDate) {
    // TODO check if baby ID != -1
    DataOperations.addWeight(currentBabyID, value, date).manage({
        sendInfoMessage(
            """Weight added!
                |Percentile: ${it.label.bold()}
            """::trimMargin
        )
    }, {
        sendErrorMessage { "Error! Cannot add weight!" }
        log.error(it.error.message)
    })
}

internal fun GrowthBot.addLength(value: Int, date: LocalDate) {
    // TODO check if baby ID != -1
    DataOperations.addLength(currentBabyID, value, date).manage({
        sendInfoMessage(
            """Length added!
                |Percentile: ${it.label.bold()}
            """::trimMargin
        )
    }, {
        sendErrorMessage { "Error! Cannot add length!" }
        log.error(it.error.message)
    })
}

internal fun GrowthBot.babyInfo() {
    // TODO check if baby ID != -1
    DataOperations.info(currentBabyID).manage({
        val notAvail = "N/A".italic()
        val days = it.birthDate.elapsedDays(LocalDate.now())
        sendInfoMessage(
            """Info about ${it.name.bold()}
                |
                |Birth date: ${it.birthDate.toDateString()} ($days day${if (days > 1) "s" else ""})
                |Sex: ${if (it.gender == Gender.MALE) "M".bold() else "F".bold()}
                |
                |${"Weight".underline()}
                |${'\t'}Measure: ${it.weight ?: notAvail} g
                |${'\t'}Date of measure: ${it.weightDate?.toDateString() ?: notAvail}
                |${'\t'}Percentile: ${it.weightPercentile ?: notAvail}
                |
                |${"Length".underline()}
                |${'\t'}Measure: ${it.length ?: notAvail} cm
                |${'\t'}Date of measure: ${it.lengthDate?.toDateString() ?: notAvail}
                |${'\t'}Percentile: ${it.lengthPercentile ?: notAvail}
            """::trimMargin
        )
    }, {
        sendErrorMessage { "Error! Cannot get info for the current baby!" }
        log.error(it.error.message)
    })
}
