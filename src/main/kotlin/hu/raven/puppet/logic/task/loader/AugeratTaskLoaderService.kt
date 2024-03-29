package hu.raven.puppet.logic.task.loader

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import hu.akos.hollo.szabo.math.FloatSumExtensions.sumClever
import hu.raven.puppet.logic.task.converter.AugeratDatasetConverterService
import hu.raven.puppet.model.task.ProcessedAugeratTask
import hu.raven.puppet.model.task.augerat.InstanceBean
import java.nio.file.Path

class AugeratTaskLoaderService(
    private val vehicleCount: Int,
    private val fileName: String,
    private val converter: AugeratDatasetConverterService,
    override val log: (String) -> Unit,
) : TaskLoaderService<ProcessedAugeratTask>() {

    override fun loadTask(folderPath: String): ProcessedAugeratTask {
        val augeratTask = loadDataFromFile(Path.of(folderPath, fileName))
        val standardTask = converter.processRawTask(augeratTask)
        standardTask.graph.edges
            .map {
                it.firstOrNull { edge -> edge.value == 0f }
            }
            .firstOrNull {
                it?.value == 0f
            }
            .let {
                println(it)
            }
        logEstimates(standardTask)
        return standardTask
    }

    override fun logEstimates(task: ProcessedAugeratTask) {
        task.graph.apply {
            log(
                "OVERESTIMATE: ${
                    (
                            edges.last().map { it.value }.sumClever()
                                    + edges.map { it.last().value }.sumClever()
                            )
                }"
            )

            log(
                "UNDERESTIMATE: ${
                    edges
                        .map { edgesFromNode ->
                            edgesFromNode.minOfOrNull { it.value } ?: 0f
                        }
                        .sumClever()

                }"
            )
        }
    }

    private fun loadDataFromFile(filePath: Path): InstanceBean {
        val xmlMapper = XmlMapper()
        return xmlMapper.readValue(
            (this.javaClass.getResource(filePath.toString())
                ?: throw Exception("Couldn't open resource!")).openStream(),
            InstanceBean::class.java
        )
    }
}