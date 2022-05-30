package org.vonvikken.growthbot

internal sealed class Try<T> {
    companion object {
        operator fun <T> invoke(block: () -> T): Try<T> = try {
            Success(block())
        } catch (exc: Exception) {
            Error(exc)
        }
    }
}

internal class Success<T>(val value: T) : Try<T>()
internal class Error<T>(val error: Exception) : Try<T>()
