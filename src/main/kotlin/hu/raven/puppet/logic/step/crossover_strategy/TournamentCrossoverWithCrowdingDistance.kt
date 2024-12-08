package hu.raven.puppet.logic.step.crossover_strategy

import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesSmaller
import hu.raven.puppet.logic.operator.crossover_operator.CrossOverOperator
import hu.raven.puppet.logic.operator.crowding_distance.CrowdingDistance
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class TournamentCrossoverWithCrowdingDistance<R>(
    override val crossoverOperators: List<CrossOverOperator<R>>,
    private val crowdingDistance: CrowdingDistance,
    private val tournamentSize: Int,
) : CrossOverStrategy<R>() {
    override fun invoke(state: EvolutionaryAlgorithmState<R>): Unit = state.run {
        val children = population.inactivesAsSequence()
            .chunked(2)
            .toList()
        val parents = population.activesAsSequence().toList()
        val crowdingDistanceOfParents = crowdingDistance(parents.map { it.value.costOrException() })

        val parentsWithCrowdingDistance = parents.indices.asSequence()
            .map { Pair(crowdingDistanceOfParents[it], parents[it]) }

        var childIndex = 0
        repeat(tournamentSize) {

            parentsWithCrowdingDistance
                .shuffled()
                .chunked(tournamentSize)
                .map { crowdingDistanceAndParent ->
                    val dominant =
                        crowdingDistanceAndParent.filter { (_, parent) ->
                            crowdingDistanceAndParent.none {
                                parent.value.costOrException() dominatesSmaller it.second.value.costOrException()
                            }
                        }

                    dominant
                        .maxBy { (crowdingDistance, _) -> crowdingDistance }
                        .second
                }
                .chunked(2)
                .forEach { parentPair ->
                    val childPair = children[childIndex]
                    childIndex++
                    crossoverOperators.first()(
                        Pair(
                            parentPair[0].value.representation,
                            parentPair[1].value.representation
                        ),
                        childPair[0].value.representation
                    )
                    crossoverOperators.first()(
                        Pair(
                            parentPair[1].value.representation,
                            parentPair[0].value.representation
                        ),
                        childPair[1].value.representation
                    )
                    childPair[0].let {
                        it.value.iterationOfCreation = state.iteration
                        it.value.cost = null
                    }
                    childPair[1].let {
                        it.value.iterationOfCreation = state.iteration
                        it.value.cost = null
                    }
                }

        }

        population.activateAll()
    }
}