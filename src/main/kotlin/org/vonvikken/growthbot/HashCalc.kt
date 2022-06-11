package org.vonvikken.growthbot

import java.security.MessageDigest

internal object HashCalc {
    private val dig: MessageDigest = MessageDigest.getInstance("SHA3-256")

    fun sha3256(value: Long): String {
        val hash: ByteArray = dig.digest(value.toString().toByteArray())
        return hash.joinToString(separator = "") { "%02x".format(it) }
    }
}
