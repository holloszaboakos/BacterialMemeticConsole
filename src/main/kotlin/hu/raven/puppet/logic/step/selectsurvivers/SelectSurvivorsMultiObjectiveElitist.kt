package hu.raven.puppet.logic.step.selectsurvivers

import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesSmaller
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class SelectSurvivorsMultiObjectiveElitist : SelectSurvivors {
    override fun invoke(state: EvolutionaryAlgorithmState): Unit = state.population.run {
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
            .takeWhile { activeCount + it.size <= poolSize / 2 }
            .forEach { it.forEach { specimen -> activate(specimen.id) } }

        frontiers
            .first { !isActive(it.first().id) }
            .shuffled()
            .slice(0..<(poolSize / 2) - activeCount)
            .forEach { specimen -> activate(specimen.id) }
    }
}