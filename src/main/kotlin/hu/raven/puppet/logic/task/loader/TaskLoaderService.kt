package hu.raven.puppet.logic.task.loader

import com.google.gson.Gson
import hu.raven.puppet.logic.logging.ObjectLoggerService
import hu.raven.puppet.model.task.Task
import java.nio.file.Path

sealed class TaskLoaderService {

    protected abstract val logger: ObjectLoggerService<String>

    abstract fun loadTask(folderPath: String): Task

    protected fun Task.isWellFormatted(): Boolean {
        return costGraph.edgesBetween.size == costGraph.objectives.size
                && costGraph.edgesBetween.all { it.size == costGraph.objectives.size - 1 }
                && costGraph.edgesFromCenter.size == costGraph.objectives.size
                && costGraph.edgesToCenter.size == costGraph.objectives.size
    }

    protected inline fun <reified T : Any> loadFromResourceFile(
        filePath: Path
    ): T {
        val resourceURL = this::class.java.getResource(filePath.toString()) ?: throw Exception("File not found")
        val gson = Gson()
        return gson.fromJson(resourceURL.readText(), T::class.java)
    }

    protected abstract fun logEstimates(task: Task)
}
