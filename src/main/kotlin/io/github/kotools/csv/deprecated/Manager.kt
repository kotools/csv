package io.github.kotools.csv.deprecated

import io.github.kotools.csv.manager.Separator
import io.github.kotools.csv.manager.comma
import io.github.kotools.csv.manager.csvFile
import io.github.kotools.csv.manager.folder

/** Configurable object responsible for manipulating CSV files. */
public sealed interface Manager {
    /**
     * **Required** property for targeting a file.
     *
     * The `.csv` extension is optional and will be added automatically in the
     * process if not present.
     * For example, `"my-file.csv"` and `"my-file"` produces the same output.
     */
    public var file: String

    /**
     * **Optional** property for targeting a folder containing the [file].
     *
     * Set to an empty string by default.
     *
     * The `'/'` suffix is optional and will be added automatically in the
     * process if not present.
     * For example, `"my-folder/"` and `"my-folder"` produces the same output.
     */
    public var folder: String

    /**
     * **Optional** property for setting the [file] content's separator.
     *
     * Set to [comma] by default.
     */
    public var separator: Separator
}

internal sealed class ManagerImplementation : Configurable, Manager {
    override var file: String by csvFile()
    override var folder: String by folder()
    override var separator: Separator = comma

    override fun isValid(): Boolean = file.isNotBlank()
}