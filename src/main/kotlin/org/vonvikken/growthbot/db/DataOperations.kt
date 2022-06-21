package org.vonvikken.growthbot.db

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.vonvikken.growthbot.Try
import org.vonvikken.growthbot.measure.DatasetName
import org.vonvikken.growthbot.measure.GrowthTables
import org.vonvikken.growthbot.measure.MeasureRange
import org.vonvikken.growthbot.measure.elapsedDays
import org.vonvikken.growthbot.successOrNull
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

    fun switchToBaby(name: String): Try<Int> = Try {
        transaction {
            return@transaction Baby.slice(Baby.id).select { Baby.name eq name }.firstOrNull()?.get(Baby.id)?.value
                ?: throw IllegalArgumentException("$name not found!")
        }
    }

    fun deleteBaby(name: String): Try<Int> = Try {
        transaction {
            return@transaction Baby.deleteWhere { Baby.name eq name }
        }
    }

    fun addWeight(babyId: Int, value: Int, date: LocalDate): Try<MeasureRange.Percentile> =
        addMeasure(Weight, babyId, value, date, { it / 1000.0 }) {
            when (it) {
                Gender.MALE -> DatasetName.WEIGHT_MALE
                Gender.FEMALE -> DatasetName.WEIGHT_FEMALE
            }
        }

    fun addLength(babyId: Int, value: Int, date: LocalDate): Try<MeasureRange.Percentile> =
        addMeasure(Length, babyId, value, date, Int::toDouble) {
            when (it) {
                Gender.MALE -> DatasetName.LENGTH_MALE
                Gender.FEMALE -> DatasetName.LENGTH_FEMALE
            }
        }

    private fun addMeasure(
        quantity: Measure,
        babyId: Int,
        value: Int,
        date: LocalDate,
        valueConversion: (Int) -> Double,
        datasetSelector: (Gender) -> DatasetName,
    ): Try<MeasureRange.Percentile> = Try {
        transaction {
            val baby = Baby.slice(Baby.gender, Baby.birthDate).select { Baby.id eq babyId }.firstOrNull()
                ?: throw IllegalArgumentException("Baby with ID $babyId not found!")
            val datasetName = datasetSelector(baby[Baby.gender])
            val day = baby[Baby.birthDate].elapsedDays(date) - 1
            val percentile = GrowthTables.getMeasurePercentile(datasetName, day, valueConversion(value)).successOrNull()
                ?: throw IllegalArgumentException("Wrong measure: $value!")
            insertMeasure(quantity, babyId, value, date, percentile)
            return@transaction percentile
        }
    }

    private fun <T : Measure> insertMeasure(
        quantity: T,
        babyId: Int,
        value: Int,
        date: LocalDate,
        percentile: MeasureRange.Percentile
    ): Int {
        return quantity.insert {
            it[quantity.babyID] = babyId
            it[quantity.measure] = value
            it[quantity.date] = date
            it[quantity.percentile] = percentile
        }.insertedCount
    }

    fun info(babyId: Int) = Try {
        transaction {
            val query =
                Baby.join(Weight, joinType = JoinType.LEFT, additionalConstraint = { Weight.babyID eq Baby.id })
                    .join(Length, joinType = JoinType.LEFT, additionalConstraint = { Length.babyID eq Baby.id })
                    .slice(
                        Baby.name,
                        Baby.birthDate,
                        Baby.gender,
                        Weight.measure,
                        Weight.date,
                        Weight.percentile,
                        Length.measure,
                        Length.date,
                        Length.percentile
                    ).select { Baby.id eq babyId }.firstOrNull()
                    ?: throw IllegalArgumentException("Baby with ID $babyId not found!")
            return@transaction InfoResult(
                query[Baby.name],
                query[Baby.birthDate],
                query[Baby.gender],
                query[Weight.measure],
                query[Weight.date],
                query[Weight.percentile],
                query[Length.measure],
                query[Length.date],
                query[Length.percentile]
            )
        }
    }
}

internal data class InfoResult(
    val name: String,
    val birthDate: LocalDate,
    val gender: Gender,
    val weight: Int?,
    val weightDate: LocalDate?,
    val weightPercentile: MeasureRange.Percentile?,
    val length: Int?,
    val lengthDate: LocalDate?,
    val lengthPercentile: MeasureRange.Percentile?
)
