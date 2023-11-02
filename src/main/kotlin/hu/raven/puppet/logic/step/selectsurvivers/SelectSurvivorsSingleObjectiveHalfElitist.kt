package hu.raven.puppet.logic.step.selectsurvivers

import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.FloatArrayExtensions.vectorLength
import hu.raven.puppet.utility.extention.slice

data object SelectSurvivorsSingleObjectiveHalfElitist : SelectSurvivors {
    override operator fun invoke(state: EvolutionaryAlgorithmState): Unit = state.population.run {
        activateAll()
        sortActiveBy { it.costOrException().vectorLength() }
        deactivateAll()
        inactivesAsSequence()
            .slice(0..<activeCount / 4)
            .forEach { activate(it.id) }
        inactivesAsSequence()
            .shuffled()
            .slice(0..<activeCount / 3)
            .forEach { activate(it.id) }
    }
}