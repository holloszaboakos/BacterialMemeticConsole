package hu.raven.puppet.logic.task.loader

import com.google.gson.Gson
import java.nio.file.Path

sealed class TaskLoaderService<T> {

    abstract val log: (String) -> Unit

    abstract fun loadTask(folderPath: String): T

    protected inline fun <reified T : Any> loadFromResourceFile(
        filePath: Path
    ): T {
        val resourceURL =
            this::class.java.getResource(filePath.toString().replace("\\", "/")) ?: throw Exception("File not found")
        val gson = Gson()
        return gson.fromJson(resourceURL.readText(), T::class.java)
    }

    protected abstract fun logEstimates(task: T)
}
