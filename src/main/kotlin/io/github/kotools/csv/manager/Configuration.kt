package io.github.kotools.csv.manager

internal class ManagerConfiguration : CsvManager {
    override var file: String by csvFile()
    override var folder: String by folder()
    override var separator: Separator = comma
}