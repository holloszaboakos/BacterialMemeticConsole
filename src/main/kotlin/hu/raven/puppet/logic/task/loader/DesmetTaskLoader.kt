package hu.raven.puppet.logic.task.loader

import hu.raven.puppet.model.task.DTask
import hu.raven.puppet.modules.FilePathVariableNames
import hu.raven.puppet.utility.desment.DesmetDatasetConverter
import hu.raven.puppet.utility.desment.DesmetDatasetLoader
import hu.raven.puppet.utility.inject

class DesmetTaskLoader : TaskLoader() {
    override fun loadTak(folderPath: String): DTask {
        val filePath: String by inject(FilePathVariableNames.DESMET_FILE)
        val task = DesmetDatasetLoader.loadDataFromFile("$folderPath\\$filePath")
        return DesmetDatasetConverter.toStandardTask(task)
    }
}