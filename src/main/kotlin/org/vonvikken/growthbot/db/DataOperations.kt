package org.vonvikken.growthbot.db

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.vonvikken.growthbot.Try
import java.time.LocalDate

internal object DataOperations {

    init {
        DatabaseConnection.db
    }

    fun addBaby(name: String, gender: Gender, birthDate: LocalDate, chatIDHash: String): Try<EntityID<Int>> = Try {
        transaction {
            Baby.insertAndGetId {
                it[Baby.name] = name
                it[Baby.gender] = gender
                it[Baby.birthDate] = birthDate
                it[chatHash] = chatIDHash
            }
        }
    }
}
