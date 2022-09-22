package hu.raven.puppet.logic.task

import hu.raven.puppet.logic.task.loader.TaskLoader
import hu.raven.puppet.model.task.DTask
import hu.raven.puppet.modules.FilePathVariableNames
import hu.raven.puppet.utility.inject

class VRPTaskHolder() {
    private val folderPath: String by inject(FilePathVariableNames.INPUT_FOLDER)
    private val taskLoader: TaskLoader by inject()
    val task: DTask = taskLoader.loadTak(folderPath)
}