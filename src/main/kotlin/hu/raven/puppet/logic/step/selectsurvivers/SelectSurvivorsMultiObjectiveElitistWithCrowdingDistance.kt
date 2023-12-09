package hu.raven.puppet.logic.step.selectsurvivers

import hu.raven.puppet.logic.operator.crowdingdistance.CrowdingDistance
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class SelectSurvivorsMultiObjectiveElitistWithCrowdingDistance(
    val crowdingDistance: CrowdingDistance
) : SelectSurvivors {
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

        if (activeCount == poolSize / 2) return@run

        val bestInactiveFrontier = frontiers.first { !isActive(it.first().id) }
        val crowdingDistanceOfBestInactive = bestInactiveFrontier
            .map { it.costOrException() }
            .let { crowdingDistance(it) }

        bestInactiveFrontier
            .withIndex()
            .sortedByDescending { crowdingDistanceOfBestInactive[it.index] }
            .slice(0..<(poolSize / 2) - activeCount)
            .forEach { specimen -> activate(specimen.value.id) }
    }
}