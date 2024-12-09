package hu.raven.puppet.logic.step.select_survivers

import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesSmaller
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class SelectSurvivorsMultiObjectiveTournament<R> : SelectSurvivors<R> {
    override fun invoke(state: EvolutionaryAlgorithmState<R>): Unit = state.population.run {
        deactivateAll()
        while (activeCount < poolSize / 2) {
            inactivesAsSequence()
                .shuffled()
                .chunked(2)
                .takeWhile { activeCount < poolSize / 2 }
                .forEach { pair ->
                    if (pair[0].value.costOrException() dominatesSmaller pair[1].value.costOrException()) {
                        activate(pair[0].index)
                    } else if (pair[1].value.costOrException() dominatesSmaller pair[0].value.costOrException()) {
                        activate(pair[1].index)
                    }
                }
        }
    }
}