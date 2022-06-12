package org.vonvikken.growthbot.measure

internal class MeasureRange(entry: MeasureEntry) {

    val day = entry.day
    private val ranges: Map<DoubleRange, Percentile>

    init {
        ranges = mapOf(
            entry.p01.lInf() to Percentile.UNDER,
            (entry.p01 ropen entry.p1) to Percentile.P_01,
            (entry.p1 ropen entry.p3) to Percentile.P_1,
            (entry.p3 ropen entry.p5) to Percentile.P_3,
            (entry.p5 ropen entry.p10) to Percentile.P_5,
            (entry.p10 ropen entry.p15) to Percentile.P_10,
            (entry.p15 ropen entry.p25) to Percentile.P_15,
            (entry.p25 ropen entry.p50) to Percentile.P_25,
            (entry.p50 ropen entry.p75) to Percentile.P_50,
            (entry.p75 ropen entry.p85) to Percentile.P_75,
            (entry.p85 ropen entry.p90) to Percentile.P_85,
            (entry.p90 ropen entry.p95) to Percentile.P_90,
            (entry.p95 ropen entry.p97) to Percentile.P_95,
            (entry.p97 ropen entry.p99) to Percentile.P_97,
            (entry.p99 ropen entry.p999) to Percentile.P_99,
            entry.p999.rInf() to Percentile.P_999
        )
    }

    fun getPercentile(measure: Double): Percentile {
        val range = ranges.keys.first { measure in it }
        // The ranges should cover all the possible real numbers, so it will always find a valid value!
        return ranges[range]!!
    }

    enum class Percentile(val label: String) {
        UNDER("<0.1%"),
        P_01("0.1%"),
        P_1("1%"),
        P_3("3%"),
        P_5("5%"),
        P_10("10%"),
        P_15("15%"),
        P_25("25%"),
        P_50("50%"),
        P_75("75%"),
        P_85("85%"),
        P_90("90%"),
        P_95("95%"),
        P_97("97%"),
        P_99("97%"),
        P_999(">=99.9%");

        override fun toString(): String = label
    }
}

internal sealed interface DoubleRange {
    operator fun contains(num: Double): Boolean
}

private data class DoubleLInfRange(val end: Double) : DoubleRange {
    override fun contains(num: Double): Boolean = num <= this.end
}

private data class DoubleRInfRange(val start: Double) : DoubleRange {
    override fun contains(num: Double): Boolean = this.start <= num
}

private data class DoubleROpenRange(val start: Double, val end: Double) : DoubleRange {
    override fun contains(num: Double): Boolean = this.start <= num && num < this.end
}

private infix fun Double.ropen(end: Double): DoubleROpenRange = DoubleROpenRange(this, end)
private fun Double.lInf(): DoubleLInfRange = DoubleLInfRange(this)
private fun Double.rInf(): DoubleRInfRange = DoubleRInfRange(this)
