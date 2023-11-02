package hu.raven.puppet.logic.step.selectsurvivers

import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.FloatArrayExtensions.compareTo
import hu.raven.puppet.utility.extention.FloatArrayExtensions.vectorLength

data object SelectSurvivorsMultiObjectiveElitist : SelectSurvivors {
    override fun invoke(state: EvolutionaryAlgorithmState): Unit = state.population.run {
        activateAll()
        sortActiveBy { it.costOrException().vectorLength() }
        deactivateAll()

        val remaining = inactivesAsSequence().toMutableList()
        val frontiers: List<List<OnePartRepresentationWithCostAndIterationAndId>> = buildList {
            while (remaining.size != 0) {
                val frontier = remaining
                    .filter { filtered ->
                        remaining
                            .none {
                                filtered.costOrException() > it.costOrException()
                            }
                    }
                add(frontier)
                remaining.removeAll(frontier)
            }
        }

        frontiers.asSequence()
            .takeWhile { activeCount + it.size <= maxSize / 2 }
            .forEach { it.forEach { specimen -> activate(specimen.id) } }

        frontiers
            .first { !isActive(it.first().id) }
            .shuffled()
            .slice(0..<(maxSize / 2) - activeCount)
    }
}