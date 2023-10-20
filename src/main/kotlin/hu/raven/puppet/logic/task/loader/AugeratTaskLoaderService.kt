package hu.raven.puppet.logic.task.loader

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import hu.raven.puppet.logic.logging.ObjectLoggerService
import hu.raven.puppet.logic.task.converter.AugeratDatasetConverterService
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.model.task.augerat.InstanceBean
import hu.raven.puppet.utility.extention.FloatSumExtensions.sumClever
import java.nio.file.Path

class AugeratTaskLoaderService(
    override val logger: ObjectLoggerService<String>,
    private val vehicleCount: Int,
    private val fileName: String,
    private val converter: AugeratDatasetConverterService,
) : TaskLoaderService() {

    override fun loadTask(folderPath: String): Task {
        val augeratTask = loadDataFromFile(Path.of(folderPath, fileName))
        val standardTask = converter.toStandardTask(augeratTask)
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
            logger.log(
                "OVERESTIMATE: ${
                    (
                            edgesFromCenter.map { it.length.value }.sumClever()
                                    + edgesToCenter.map { it.length.value }.sumClever()
                            )
                }"
            )

            logger.log(
                "UNDERESTIMATE: ${
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

    private fun loadDataFromFile(filePath: Path): InstanceBean {
        val xmlMapper = XmlMapper()
        return xmlMapper.readValue(
            this.javaClass.getResource(filePath.toString())!!.openStream(),
            InstanceBean::class.java
        )
    }
}