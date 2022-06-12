package org.vonvikken.growthbot.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.date
import org.vonvikken.growthbot.measure.MeasureRange
import java.time.LocalDate

internal sealed class Measure(name: String) : IntIdTable(name = name) {
    val babyID = reference(name = "baby_id", refColumn = id)
    val measure: Column<Int> = integer("measure")
    val date: Column<LocalDate> = date("date")
    val percentile: Column<MeasureRange.Percentile> = enumeration("percentile", MeasureRange.Percentile::class)
}

internal object Length : Measure("Length")

internal object Weight : Measure("Weight")
