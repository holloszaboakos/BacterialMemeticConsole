package hu.raven.puppet.logic.task.loader

import hu.raven.puppet.model.task.Task
import hu.raven.puppet.modules.FilePathVariableNames
import hu.raven.puppet.utility.dataset.desment.DesmetDatasetConverter
import hu.raven.puppet.utility.dataset.desment.DesmetDatasetLoader
import hu.raven.puppet.utility.extention.min
import hu.raven.puppet.utility.extention.sumClever
import hu.raven.puppet.utility.inject

class DesmetTaskLoader : TaskLoader() {
    override fun loadTask(folderPath: String): Task {
        val filePath: String by inject(FilePathVariableNames.SINGLE_FILE)
        val desmetTask = DesmetDatasetLoader.loadDataFromFile("/$folderPath/$filePath")
        val standardTask = DesmetDatasetConverter.toStandardTask(desmetTask)
        logEstimates(standardTask)
        return standardTask
    }

    override fun logEstimates(task: Task) {
        task.costGraph.apply {
            doubleLogger(
                "OVERASTIMATE: ${
                    (
                            edgesFromCenter.map { it.length.value }.sumClever()
                                    + edgesToCenter.map { it.length.value }.sumClever()
                            )
                }"
            )

            doubleLogger(
                "UNDERASTIMATE: ${
                    (
                            edgesFromCenter.map { it.length.value }.min() +
                                    edgesBetween.mapIndexed { index, edge ->
                                        arrayOf(
                                            edge.map { it.length.value }.min(),
                                            edgesToCenter[index].length.value
                                        ).min()
                                    }.sumClever()
                            )
                }"
            )
        }
    }
}