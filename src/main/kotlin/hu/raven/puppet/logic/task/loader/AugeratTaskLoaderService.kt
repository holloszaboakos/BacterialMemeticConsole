package hu.raven.puppet.logic.task.loader

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import hu.akos.hollo.szabo.math.FloatSumExtensions.preciseSum
import hu.raven.puppet.logic.task.converter.AugeratDatasetConverterService
import hu.raven.puppet.model.dataset.ProcessedAugeratTask
import hu.raven.puppet.model.dataset.augerat.InstanceBean
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
            .asSequence()
            .map {
                it.asList().firstOrNull { edge -> edge == 0f }
            }
            .firstOrNull {
                it == 0f
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
                            edges.asList().last().asSequence().map { it }.preciseSum()
                                    + edges.asSequence().map { it.asList().last() }.preciseSum()
                            )
                }"
            )

            log(
                "UNDERESTIMATE: ${
                    edges
                        .asSequence()
                        .map { edgesFromNode ->
                            edgesFromNode.asList().minOfOrNull { it } ?: 0f
                        }
                        .preciseSum()

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