package org.vonvikken.growthbot.bot

import org.vonvikken.growthbot.bold
import org.vonvikken.growthbot.db.DataOperations
import org.vonvikken.growthbot.db.Gender
import org.vonvikken.growthbot.italic
import org.vonvikken.growthbot.logger
import org.vonvikken.growthbot.manage
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
