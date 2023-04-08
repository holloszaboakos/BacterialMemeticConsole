package hu.raven.puppet.logic.task.loader

import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.modules.FilePathVariableNames
import hu.raven.puppet.utility.dataset.augerat.AugeratDatasetConverter
import hu.raven.puppet.utility.dataset.augerat.AugeratDatasetLoader
import hu.raven.puppet.utility.extention.sumClever
import hu.raven.puppet.utility.inject

class AugeratTaskLoader : TaskLoader() {
    private val vehicleCount: Int by inject(AlgorithmParameters.VEHICLE_COUNT)

    override fun loadTask(folderPath: String): Task {
        val filePath: String by inject(FilePathVariableNames.SINGLE_FILE)
        val augeratTask = AugeratDatasetLoader.loadDataFromFile("/$folderPath/$filePath")
        val standardTask = AugeratDatasetConverter.toStandardTask(augeratTask)
        standardTask.costGraph.edgesBetween
            .map {
                it.firstOrNull { edge -> edge.length == Meter(0) }
            }
            .firstOrNull {
                it?.length == Meter(0)
            }
            .let {
                println(it)
            }
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
                    ((
                            edgesFromCenter.map { it.length.value }.sumClever() +
                                    edgesBetween.mapIndexed { index, edge ->
                                        arrayOf(
                                            edge.map { it.length.value }.min(),
                                            edgesToCenter[index].length.value
                                        ).min()
                                    }.sumClever()
                            ) / vehicleCount.toLong())
                }"
            )
        }
    }
}