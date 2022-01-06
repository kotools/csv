package io.github.kotools.csv.writer

import com.github.doyaaaaaken.kotlincsv.client.CsvWriter
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import io.github.kotools.csv.common.DataType
import io.github.kotools.csv.common.ListHolder
import io.github.kotools.csv.common.ManagerImplementation
import io.github.kotools.csv.common.Target
import io.github.kotools.csv.common.createTarget
import io.github.kotools.csv.common.filePath
import io.github.kotools.csv.common.findTargetOrNull
import io.github.kotools.csv.common.invalidConfigurationError
import io.github.kotools.csv.common.toDataType
import java.io.File
import kotlin.reflect.KClass

private val Writer<*>.csv: CsvWriter
    get() = csvWriter { delimiter = separator.value }

internal infix fun <T : Any> KClass<T>.processWriter(
    configuration: Writer<T>.() -> Unit
) {
    val dataType: DataType<T> = toDataType()
    val writer: WriterImplementation<T> = WriterImplementation<T>()
        .apply(configuration)
    if (!writer.isValid()) invalidConfigurationError()
    val target: Target =
        findTargetOrNull(writer.filePath) ?: writer.createTarget()
    val file: File = if (target is Target.File) target.file else return
    val records: MutableList<List<String>> =
        if (writer.overwrite) mutableListOf(dataType.constructorParameters)
        else mutableListOf()
    records += writer.records.map(dataType::getValuesOf)
    writer.csv.writeAll(records, file, !writer.overwrite)
}

internal infix fun <T : Any> KClass<T>.processWriterOrNull(
    configuration: Writer<T>.() -> Unit
): Unit? = try {
    processWriter(configuration)
} catch (exception: IllegalStateException) {
    null
}

private class WriterImplementation<T : Any> :
    ManagerImplementation(),
    Writer<T> {
    val records: List<T> get() = recordsHolder!!.items

    override var overwrite: Boolean = true
    private var recordsHolder: RecordsHolder<T>? = null

    fun isValid(): Boolean = file.isNotBlank() && recordsHolder != null

    override fun records(configuration: Writer.Records<T>.() -> Unit) {
        recordsHolder = RecordsHolder<T>()
            .apply(configuration)
            .takeIf(RecordsHolder<T>::isValid)
    }

    private class RecordsHolder<T : Any> : ListHolder<T>(), Writer.Records<T>
}