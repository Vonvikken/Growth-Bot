package org.vonvikken.growthbot.measure

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class MeasureEntry(
    @SerialName("Day") val day: Int,
    @SerialName("L") val l: Double,
    @SerialName("M") val m: Double,
    @SerialName("S") val s: Double,
    @SerialName("P01") val p01: Double,
    @SerialName("P1") val p1: Double,
    @SerialName("P3") val p3: Double,
    @SerialName("P5") val p5: Double,
    @SerialName("P10") val p10: Double,
    @SerialName("P15") val p15: Double,
    @SerialName("P25") val p25: Double,
    @SerialName("P50") val p50: Double,
    @SerialName("P75") val p75: Double,
    @SerialName("P85") val p85: Double,
    @SerialName("P90") val p90: Double,
    @SerialName("P95") val p95: Double,
    @SerialName("P97") val p97: Double,
    @SerialName("P99") val p99: Double,
    @SerialName("P999") val p999: Double,
)
