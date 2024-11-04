package hu.raven.puppet.logic.step.select_survivers

import hu.akos.hollo.szabo.collections.slice
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesSmaller
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

data object SelectSurvivorsMultiObjectiveHalfElitist : SelectSurvivors {
    override operator fun invoke(state: EvolutionaryAlgorithmState<*>): Unit = state.population.run {
        deactivateAll()


        val remaining = inactivesAsSequence().toMutableList()
        val frontiers: List<List<IndexedValue<OnePartRepresentationWithCostAndIteration>>> = buildList {
            while (remaining.size != 0) {
                val frontier = remaining
                    .filter { (_,filtered) ->
                        remaining
                            .none {
                                filtered.costOrException() dominatesSmaller it.value.costOrException()
                            }
                    }
                add(frontier)
                remaining.removeAll(frontier)
            }
        }

        frontiers.asSequence()
            .takeWhile { activeCount + it.size <= poolSize / 4 }
            .forEach { it.forEach { specimen -> activate(specimen.index) } }

        frontiers
            .first { !isActive(it.first().index) }
            .shuffled()
            .slice(0..<(poolSize / 4) - activeCount)
            .forEach { activate(it.index) }

        inactivesAsSequence()
            .shuffled()
            .slice(0..<poolSize / 4)
            .forEach { activate(it.index) }
    }
}