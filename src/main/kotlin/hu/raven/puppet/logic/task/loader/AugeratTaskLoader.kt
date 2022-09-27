package hu.raven.puppet.logic.task.loader

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
                it.values.firstOrNull { edge -> edge.length_Meter == 0L }
            }
            .firstOrNull {
                it?.length_Meter == 0L
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
                edgesFromCenter.sumOf { it.length_Meter }
                        + edgesToCenter.sumOf { it.length_Meter }
            }")

            doubleLogger("UNDERASTIMATE: ${
                (
                        edgesFromCenter.minOf { it.length_Meter } +
                                edgesBetween.sumOf { edge ->
                                    min(
                                        edge.values.minOf { it.length_Meter },
                                        edgesToCenter[edge.orderInOwner].length_Meter
                                    )
                                }
                        ) / vehicleCount
            }")
        }
    }
}