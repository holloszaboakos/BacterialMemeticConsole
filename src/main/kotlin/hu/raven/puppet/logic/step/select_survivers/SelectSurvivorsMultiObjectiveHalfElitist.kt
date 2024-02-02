package hu.raven.puppet.logic.step.select_survivers

import hu.akos.hollo.szabo.collections.slice
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesSmaller
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

data object SelectSurvivorsMultiObjectiveHalfElitist : SelectSurvivors {
    override operator fun invoke(state: EvolutionaryAlgorithmState): Unit = state.population.run {
        deactivateAll()


        val remaining = inactivesAsSequence().toMutableList()
        val frontiers: List<List<OnePartRepresentationWithCostAndIterationAndId>> = buildList {
            while (remaining.size != 0) {
                val frontier = remaining
                    .filter { filtered ->
                        remaining
                            .none {
                                filtered.costOrException() dominatesSmaller it.costOrException()
                            }
                    }
                add(frontier)
                remaining.removeAll(frontier)
            }
        }

        frontiers.asSequence()
            .takeWhile { activeCount + it.size <= poolSize / 4 }
            .forEach { it.forEach { specimen -> activate(specimen.id) } }

        frontiers
            .first { !isActive(it.first().id) }
            .shuffled()
            .slice(0..<(poolSize / 4) - activeCount)
            .forEach { activate(it.id) }

        inactivesAsSequence()
            .shuffled()
            .slice(0..<poolSize / 4)
            .forEach { activate(it.id) }
    }
}