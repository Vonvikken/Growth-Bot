package org.vonvikken.growthbot

internal sealed class Try<T> {
    companion object {

        operator fun <T> invoke(block: () -> T): Try<T> = try {
            Success(block())
        } catch (exc: Exception) {
            Error(exc)
        }
    }

    internal class Success<T>(val value: T) : Try<T>()
    internal class Error<T>(val error: Exception) : Try<T>()

    @Suppress("IMPLICIT_CAST_TO_ANY")
    override fun toString(): String = when (this) {
        is Success -> value
        is Error -> error
    }.toString()
}

internal fun <T> Try<T>.orElseGet(default: T): T = when (this) {
    is Try.Success -> value
    is Try.Error -> default
}

internal fun <T> Try<T>.successOrNull(): T? = (this as? Try.Success)?.value

internal fun <T, U> Try<T>.manage(onSuccess: (T) -> U, onError: (Try.Error<T>) -> U): U = when (this) {
    is Try.Success -> onSuccess(this.value)
    is Try.Error -> onError(this)
}
