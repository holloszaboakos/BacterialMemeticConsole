package hu.raven.puppet.logic.step.crossoverstrategy

import hu.raven.puppet.logic.operator.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.operator.crowdingdistance.CrowdingDistance
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.FloatArrayExtensions.compareTo

class TournamentCrossoverWithCrowdingDistance(
    override val crossoverOperators: List<CrossOverOperator>,
    private val crowdingDistance: CrowdingDistance,
    private val tournamentSize: Int,
) : CrossOverStrategy() {
    override fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        val children = population.inactivesAsSequence()
            .chunked(2)
            .toList()
        val parents = population.activesAsSequence().toList()
        val crowdingDistanceOfParents = crowdingDistance(parents.map { it.costOrException() })

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
                                parent.costOrException() < it.second.costOrException()
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
                            parentPair[0].permutation,
                            parentPair[1].permutation
                        ),
                        childPair[0].permutation
                    )
                    crossoverOperators.first()(
                        Pair(
                            parentPair[1].permutation,
                            parentPair[0].permutation
                        ),
                        childPair[1].permutation
                    )
                    childPair[0].let {
                        it.iterationOfCreation = state.iteration
                        it.cost = null
                        if (!it.permutation.checkFormat())
                            throw Error("Invalid specimen!")
                    }
                    childPair[1].let {
                        it.iterationOfCreation = state.iteration
                        it.cost = null
                        if (!it.permutation.checkFormat())
                            throw Error("Invalid specimen!")
                    }
                }

        }

        population.activateAll()
    }
}