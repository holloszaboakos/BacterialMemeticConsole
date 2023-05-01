package hu.raven.puppet.logic.step.selectsurvivers

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.slice

class SelectSurvivors : EvolutionaryAlgorithmStep {
    override operator fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        population.deactivateAll()
        population.activesAsSequence()
            .slice(0 until population.activeCount / 4)
            .forEach { population.activate(it.id) }

        population.activesAsSequence()
            .slice(population.activeCount / 4 until population.activeCount)
            .shuffled()
            .slice(0 until population.activeCount / 4)
            .forEach { population.activate(it.id) }
    }
}