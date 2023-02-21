package hu.raven.puppet.logic.task.loader

import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.task.DTask
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.modules.FilePathVariableNames
import hu.raven.puppet.utility.dataset.augerat.AugeratDatasetConverter
import hu.raven.puppet.utility.dataset.augerat.AugeratDatasetLoader
import hu.raven.puppet.utility.inject
import kotlin.math.min

class AugeratTaskLoader : TaskLoader() {
    private val vehicleCount: Int by inject(AlgorithmParameters.VEHICLE_COUNT)

    override fun loadTak(folderPath: String): DTask {
        val filePath: String by inject(FilePathVariableNames.SINGLE_FILE)
        val augeratTask = AugeratDatasetLoader.loadDataFromFile("$folderPath\\$filePath")
        val standardTask = AugeratDatasetConverter.toStandardTask(augeratTask)
        standardTask.costGraph.edgesBetween
            .map {
                it.values.firstOrNull { edge -> edge.length == Meter(0) }
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

    override fun logEstimates(task: DTask) {
        task.costGraph.apply {
            doubleLogger("OVERASTIMATE: ${
                edgesFromCenter.sumOf { it.length.value.toDouble() }
                        + edgesToCenter.sumOf { it.length.value.toDouble() }
            }")

            doubleLogger("UNDERASTIMATE: ${
                (
                        edgesFromCenter.minOf { it.length.value.toDouble() } +
                                edgesBetween.sumOf { edge ->
                                    min(
                                        edge.values.minOf { it.length.value.toDouble() },
                                        edgesToCenter[edge.orderInOwner].length.value.toDouble()
                                    )
                                }
                        ) / vehicleCount
            }")
        }
    }
}