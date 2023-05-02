package hu.raven.puppet.logic.operator.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.task.CostGraph

sealed class CrossOverOperator {
    companion object {
        fun getVariants(
            costGraph: CostGraph
        ) = listOf(
            AlternatingEdgeCrossOver,
            AlternatingPositionCrossOver,
            CycleCrossOver,
            DistancePreservingCrossOver,
            GeneticEdgeRecombinationCrossOver,
            HeuristicCrossOver(costGraph),
            MaximalPreservationCrossOver,
            OrderBasedCrossOver,
            OrderCrossOver,
            PartiallyMatchedCrossOver,
            PositionBasedCrossOver,
            SortedMatchCrossOver(costGraph),
            SubTourChunksCrossOver,
            VotingRecombinationCrossOver,
        )
    }

    abstract operator fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    )
}