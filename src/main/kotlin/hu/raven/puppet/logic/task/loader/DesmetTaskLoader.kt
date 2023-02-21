package hu.raven.puppet.logic.task.loader

import hu.raven.puppet.model.task.DTask
import hu.raven.puppet.modules.FilePathVariableNames
import hu.raven.puppet.utility.dataset.desment.DesmetDatasetConverter
import hu.raven.puppet.utility.dataset.desment.DesmetDatasetLoader
import hu.raven.puppet.utility.inject
import kotlin.math.min

class DesmetTaskLoader : TaskLoader() {
    override fun loadTak(folderPath: String): DTask {
        val filePath: String by inject(FilePathVariableNames.SINGLE_FILE)
        val desmetTask = DesmetDatasetLoader.loadDataFromFile("$folderPath\\$filePath")
        val standardTask = DesmetDatasetConverter.toStandardTask(desmetTask)
        logEstimates(standardTask)
        return standardTask
    }

    override fun logEstimates(task: DTask) {
        task.costGraph.apply {
            doubleLogger("OVERASTIMATE: ${
                edgesFromCenter.sumOf { it.length.value.toDouble() }
                        + edgesToCenter.sumOf { it.length.value.toDouble() }
            }")

            doubleLogger("UNDERASTIMATE: ${
                edgesFromCenter.minOf { it.length.value.toDouble() } +
                        edgesBetween.sumOf { edge ->
                            min(
                                edge.values.minOf { it.length.value.toDouble() },
                                edgesToCenter[edge.orderInOwner].length.value.toDouble()
                            )
                        }
            }")
        }
    }
}