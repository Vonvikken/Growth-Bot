package org.vonvikken.growthbot

internal sealed class Try<T> {
    companion object {
        operator fun <T> invoke(block: () -> T): Try<T> = try {
            Success(block())
        } catch (exc: Exception) {
            Error(exc)
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    override fun toString(): String = when (this) {
        is Success -> value
        is Error -> error
    }.toString()
}

internal fun <T> Try<T>.orElseGet(default: T): T = when (this) {
    is Success -> value
    is Error -> default
}

internal class Success<T>(val value: T) : Try<T>()
internal class Error<T>(val error: Exception) : Try<T>()
