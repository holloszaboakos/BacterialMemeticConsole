package hu.raven.puppet.logic

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.model.mtsp.DTask
import org.koin.java.KoinJavaComponent.inject

class AlgorithmManagerService {

    val algorithm: SEvolutionaryAlgorithm<*> by inject(SEvolutionaryAlgorithm::class.java)
    var task: DTask? = null

    fun checkIfTaskIsWellFormatted(): Boolean {
        return task?.let { task ->
            task.costGraph.edgesBetween.size == task.costGraph.objectives.size
                    && task.costGraph.edgesBetween.all { it.values.size == task.costGraph.objectives.size - 1 }
                    && task.costGraph.edgesFromCenter.size == task.costGraph.objectives.size
                    && task.costGraph.edgesToCenter.size == task.costGraph.objectives.size
        } == true
    }


    fun initializeAlgorithm() {
        task?.let { task ->
            algorithm.costGraph = task.costGraph
            algorithm.salesmen = task.salesmen
        }
        algorithm.initialize()
    }

    fun runIteration() = algorithm.iterate(true)
}