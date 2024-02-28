package hu.raven.puppet.logic.step.select_survivers

import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesSmaller
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


data object SelectSurvivorsMultiObjectiveTournament : SelectSurvivors {
    override fun invoke(state: EvolutionaryAlgorithmState<*>): Unit = state.population.run {
        deactivateAll()
        while (activeCount < poolSize / 2) {
            inactivesAsSequence()
                .shuffled()
                .chunked(2)
                .takeWhile { activeCount < poolSize / 2 }
                .forEach { pair ->
                    if (pair[0].costOrException() dominatesSmaller pair[1].costOrException()) {
                        activate(pair[0].id)
                    } else if (pair[1].costOrException() dominatesSmaller pair[0].costOrException()) {
                        activate(pair[1].id)
                    }
                }
        }
    }
}