package hu.raven.puppet.logic.step.selectsurvivers

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.slice

class SelectSurvivors : EvolutionaryAlgorithmStep {
    override operator fun invoke(state: EvolutionaryAlgorithmState): Unit = state.population.run {
        activateAll()
        sortActiveBy { it.costOrException() }
        deactivateAll()
        inactivesAsSequence()
            .slice(0 until activeCount / 4)
            .forEach { activate(it.id) }
        inactivesAsSequence()
            .shuffled()
            .slice(0 until activeCount / 3)
            .forEach { activate(it.id) }
    }
}