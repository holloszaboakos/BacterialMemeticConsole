package hu.raven.puppet.logic.task.loader

import hu.raven.puppet.model.task.DTask
import hu.raven.puppet.modules.FilePathVariableNames
import hu.raven.puppet.utility.dataset.desment.DesmetDatasetConverter
import hu.raven.puppet.utility.dataset.desment.DesmetDatasetLoader
import hu.raven.puppet.utility.extention.min
import hu.raven.puppet.utility.extention.sumClever
import hu.raven.puppet.utility.inject

class DesmetTaskLoader : TaskLoader() {
    override fun loadTak(folderPath: String): DTask {
        val filePath: String by inject(FilePathVariableNames.SINGLE_FILE)
        val desmetTask = DesmetDatasetLoader.loadDataFromFile("/$folderPath/$filePath")
        val standardTask = DesmetDatasetConverter.toStandardTask(desmetTask)
        logEstimates(standardTask)
        return standardTask
    }

    override fun logEstimates(task: DTask) {
        task.costGraph.apply {
            doubleLogger(
                "OVERASTIMATE: ${
                    (
                            edgesFromCenter.map { it.length.value }.sumClever()
                                    + edgesToCenter.map { it.length.value }.sumClever()
                            ).toDouble()
                }"
            )

            doubleLogger(
                "UNDERASTIMATE: ${
                    (
                            edgesFromCenter.map { it.length.value }.min() +
                                    edgesBetween.map { edge ->
                                        arrayOf(
                                            edge.values.map { it.length.value }.min(),
                                            edgesToCenter[edge.orderInOwner].length.value
                                        ).min()
                                    }.sumClever()
                            ).toDouble()
                }"
            )
        }
    }
}