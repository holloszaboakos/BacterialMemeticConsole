package hu.raven.puppet.logic.task

import com.google.gson.Gson
import hu.raven.puppet.model.task.DSalesman
import hu.raven.puppet.model.task.DTask
import hu.raven.puppet.model.task.graph.DEdge
import hu.raven.puppet.model.task.graph.DEdgeArray
import hu.raven.puppet.model.task.graph.DGraph
import hu.raven.puppet.model.task.graph.DObjective
import hu.raven.puppet.modules.FilePathVariableNames
import hu.raven.puppet.utility.inject
import java.io.File

class VRPTaskHolder() {
    private val folderPath: String by inject(FilePathVariableNames.INPUT_FOLDER)
    val task: DTask

    init {
        val incompleteGraph: DGraph =
            loadFromResourceFile(folderPath, FilePathVariableNames.GRAPH_FILE)
        val edgesBetween: Array<DEdgeArray> =
            loadFromResourceFile(folderPath, FilePathVariableNames.EDGES_BETWEEN_FILE)
        val edgesFromCenter: Array<DEdge> =
            loadFromResourceFile(folderPath, FilePathVariableNames.EDGES_FROM_CENTER_FILE)
        val edgesToCenter: Array<DEdge> =
            loadFromResourceFile(folderPath, FilePathVariableNames.EDGES_TO_CENTER_FILE)
        val salesmen: Array<DSalesman> =
            loadFromResourceFile(folderPath, FilePathVariableNames.SALESMAN_FILE)
        val objectives: Array<DObjective> =
            loadFromResourceFile(folderPath, FilePathVariableNames.OBJECTIVES_FILE)

        task = DTask(
            salesmen = salesmen,
            costGraph = incompleteGraph.copy(
                objectives = objectives,
                edgesBetween = edgesBetween,
                edgesFromCenter = edgesFromCenter,
                edgesToCenter = edgesToCenter
            )
        )

        if(!isTaskWellFormatted()){
            throw Exception("Task is wrongly formatted!")
        }
    }

    private fun isTaskWellFormatted(): Boolean {
        return task.costGraph.edgesBetween.size == task.costGraph.objectives.size
                && task.costGraph.edgesBetween.all { it.values.size == task.costGraph.objectives.size - 1 }
                && task.costGraph.edgesFromCenter.size == task.costGraph.objectives.size
                && task.costGraph.edgesToCenter.size == task.costGraph.objectives.size
    }

    private inline fun <reified T : Any> loadFromResourceFile(
        folderPath: String,
        argumentName: FilePathVariableNames
    ): T {
        val filePath: String by inject(argumentName)
        val file = File("$folderPath\\$filePath")
        val gson = Gson()
        return gson.fromJson(file.readText(), T::class.java)
    }
}