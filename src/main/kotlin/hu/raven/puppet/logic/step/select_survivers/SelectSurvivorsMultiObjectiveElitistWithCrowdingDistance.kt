package hu.raven.puppet.logic.step.select_survivers

import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesSmaller
import hu.raven.puppet.logic.operator.crowding_distance.CrowdingDistance
import hu.raven.puppet.model.solution.SolutionWithIteration
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class SelectSurvivorsMultiObjectiveElitistWithCrowdingDistance<R>(
    val crowdingDistance: CrowdingDistance
) : SelectSurvivors<R> {
    override fun invoke(state: EvolutionaryAlgorithmState<R>): Unit = state.population.run {
        deactivateAll()

        val remaining = inactivesAsSequence().toMutableList()
        val frontiers: List<List<IndexedValue<SolutionWithIteration<*>>>> = buildList {
            while (remaining.size != 0) {
                val frontier = remaining
                    .filter { filtered ->
                        remaining
                            .none {
                                filtered.value.costOrException() dominatesSmaller it.value.costOrException()
                            }
                    }
                add(frontier)
                remaining.removeAll(frontier)
            }
        }

        frontiers.asSequence()
            .takeWhile { activeCount + it.size <= poolSize / 2 }
            .forEach { it.forEach { specimen -> activate(specimen.index) } }

        if (activeCount == poolSize / 2) return@run

        val bestInactiveFrontier = frontiers.first { !isActive(it.first().index) }
        val crowdingDistanceOfBestInactive = bestInactiveFrontier
            .map { it.value.costOrException() }
            .let { crowdingDistance(it) }

        bestInactiveFrontier
            .withIndex()
            .sortedByDescending { crowdingDistanceOfBestInactive[it.index] }
            .slice(0..<(poolSize / 2) - activeCount)
            .forEach { specimen -> activate(specimen.value.index) }
    }
}