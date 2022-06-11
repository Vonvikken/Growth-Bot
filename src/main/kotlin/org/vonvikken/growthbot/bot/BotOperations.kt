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
    DataOperations.addBaby(name, gender, birthDate, chatIDHash).manage({ entityID ->
        currentBabyID = entityID.value
        sendInfoMessage { "${name.bold()} added!" }
    }, {
        sendErrorMessage { "Error! Cannot add ${name.italic()}!" }
        log.error(it.error.message)
    })
}
