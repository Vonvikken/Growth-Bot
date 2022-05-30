package org.vonvikken.growthbot.measure

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.csv.Csv
import org.vonvikken.growthbot.Try
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
internal object GrowthTables {
    val csv = Csv { hasHeaderRecord = true }
    private val tables: Map<DatasetName, GrowthTable> =
        DatasetName.values().associateBy({ it }, GrowthTable.Companion::loadDataset)

    fun getMeasurePercentile(datasetName: DatasetName, day: Int, measure: Double): Try<MeasureRange.Percentile> =
        Try { tables[datasetName]!!.getPercentile(day, measure) }
}

@OptIn(ExperimentalSerializationApi::class)
internal class GrowthTable private constructor(list: List<MeasureEntry>) {
    private val data: Map<Int, MeasureRange> = list.associateBy({ it.day }, ::MeasureRange)

    companion object {
        fun loadDataset(datasetName: DatasetName): GrowthTable {
            val list = this::class.java.getResource("/tables/${datasetName.fileName}.csv")?.let {
                return@let GrowthTables.csv.decodeFromString(
                    ListSerializer(MeasureEntry.serializer()),
                    File(it.toURI()).readText()
                )
            } ?: emptyList()

            return GrowthTable(list)
        }
    }

    fun getPercentile(day: Int, measure: Double): MeasureRange.Percentile =
        data[day]?.getPercentile(measure) ?: throw IllegalArgumentException("Day $day not found!")
}
