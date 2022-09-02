package hu.raven.puppet.logic

import hu.raven.puppet.logic.common.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.evolutionary.common.iteration.EvolutionaryIteration
import hu.raven.puppet.model.task.DTask
import org.koin.java.KoinJavaComponent.inject

class AlgorithmManagerService {

    val algorithm: SEvolutionaryAlgorithm<*> by inject(SEvolutionaryAlgorithm::class.java)
    val initialize: InitializeAlgorithm<*> by inject(InitializeAlgorithm::class.java)
    val iterate: EvolutionaryIteration<*> by inject(EvolutionaryIteration::class.java)
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
            algorithm.task = task
        }
        initialize()
    }

    fun runIteration() = iterate(true)
}