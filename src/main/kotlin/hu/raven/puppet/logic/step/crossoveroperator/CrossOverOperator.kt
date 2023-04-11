package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit

import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

sealed class CrossOverOperator<C : PhysicsUnit<C>> {
    companion object {
        fun <C : PhysicsUnit<C>> getVariants(
            algorithmState: EvolutionaryAlgorithmState<C>,
        ) = listOf<CrossOverOperator<C>>(
            AlternatingEdgeCrossOver(),
            AlternatingPositionCrossOver(),
            CycleCrossOver(),
            DistancePreservingCrossOver(),
            GeneticEdgeRecombinationCrossOver(),
            HeuristicCrossOver { algorithmState.task.costGraph }, //TODO provider
            MaximalPreservationCrossOver(),
            OrderBasedCrossOver(),
            OrderCrossOver(),
            PartiallyMatchedCrossOver(),
            PositionBasedCrossOver(),
            //broken SortedMatchCrossOver,
            SubTourChunksCrossOver(),
            VotingRecombinationCrossOver(),
        )
    }

    abstract operator fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    )
}