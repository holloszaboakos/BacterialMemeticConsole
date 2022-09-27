package hu.raven.puppet.logic.task.loader

import com.google.gson.Gson
import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.model.task.DTask
import hu.raven.puppet.modules.FilePathVariableNames
import hu.raven.puppet.utility.inject
import java.io.File
import kotlin.math.min

sealed class TaskLoader {

    protected val doubleLogger: DoubleLogger by inject()

    abstract fun loadTak(folderPath: String): DTask

    protected fun DTask.isWellFormatted(): Boolean {
        return costGraph.edgesBetween.size == costGraph.objectives.size
                && costGraph.edgesBetween.all { it.values.size == costGraph.objectives.size - 1 }
                && costGraph.edgesFromCenter.size == costGraph.objectives.size
                && costGraph.edgesToCenter.size == costGraph.objectives.size
    }

    protected inline fun <reified T : Any> loadFromResourceFile(
        folderPath: String,
        argumentName: FilePathVariableNames
    ): T {
        val filePath: String by inject(argumentName)
        val file = File("$folderPath\\$filePath")
        val gson = Gson()
        return gson.fromJson(file.readText(), T::class.java)
    }

    abstract fun logEstimates(task: DTask)
}
