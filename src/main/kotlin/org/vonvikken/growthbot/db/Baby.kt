package org.vonvikken.growthbot.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

internal object Baby : IntIdTable(name = "Baby", columnName = "baby_id") {
    val chatHash: Column<String> = char("chat_id_hash", length = 40)
    val name: Column<String> = text("name")
    val birthDate: Column<LocalDate> = date("birth_date")
    val gender: Column<Gender> = enumeration("gender", Gender::class)

    init {
        uniqueIndex(chatHash, name, birthDate)
    }
}

internal enum class Gender {
    MALE, FEMALE
}
