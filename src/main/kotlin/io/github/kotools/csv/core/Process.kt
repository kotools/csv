package io.github.kotools.csv.core

internal fun interface Process<out R> {
    fun process(): R?
}