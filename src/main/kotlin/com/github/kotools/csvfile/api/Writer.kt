package com.github.kotools.csvfile.api

import com.github.kotools.csvfile.core.writer
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

// TODO: File exists in resources ? write in it : look in system file

// TODO: Add tests
public suspend fun csvWriter(config: WriterApi.() -> Unit): Unit? =
    withContext(IO) { writer(config) }

public abstract class WriterApi : CsvApi() {
    public var header: Set<String> = emptySet()
    public var overwrite: Boolean = true
    protected var rowsApi: WriterRowsApi? = null

    public fun rows(config: WriterRowsApi.() -> Unit) {
        rowsApi = object : WriterRowsApi() {}.apply(config)
    }
}

public abstract class WriterRowsApi : CsvApi() {
    private val _rows: MutableList<List<String>> = mutableListOf()

    internal val rows: List<List<String>> get() = _rows

    public operator fun List<String>.unaryPlus() {
        _rows += this
    }
}