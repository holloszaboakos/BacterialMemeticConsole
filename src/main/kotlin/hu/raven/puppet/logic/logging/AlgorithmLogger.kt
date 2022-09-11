package hu.raven.puppet.logic.logging

import hu.raven.puppet.modules.FilePathVariableNames
import hu.raven.puppet.utility.inject
import java.io.File
import java.time.LocalDateTime

sealed class AlgorithmLogger{
    protected abstract val targetFile: File
    protected val outputFolderPath: String by inject(FilePathVariableNames.OUTPUT_FOLDER)
    protected val creationTime : String = LocalDateTime.now()
        .toString()
        .split('.')[0]
        .replace(':', '-')

}
