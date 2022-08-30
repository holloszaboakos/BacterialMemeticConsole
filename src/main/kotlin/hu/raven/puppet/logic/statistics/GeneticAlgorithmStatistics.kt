package hu.raven.puppet.logic.statistics

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator.*
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class GeneticAlgorithmStatistics<S : ISpecimenRepresentation>(
    val algorithm: GeneticAlgorithm<S>
) : AlgorithmStatistics {
    override var diversity = Double.MAX_VALUE
    val operatorsWithStatistics =
        listOf(
            AlternatingEdgeCrossOver(algorithm),
            AlternatingPositionCrossOver(algorithm),
            CycleCrossOver(algorithm),
            DistancePreservingCrossOver(algorithm),
            GeneticEdgeRecombinationCrossOver(algorithm),
            HeuristicCrossOver(algorithm),
            MaximalPreservationCrossOver(algorithm),
            OrderBasedCrossOver(algorithm),
            OrderCrossOver(algorithm),
            PartiallyMatchedCrossOver(algorithm),
            PositionBasedCrossOver(algorithm),
            //broken SortedMatchCrossOver,
            SubTourChunksCrossOver(algorithm),
            VotingRecombinationCrossOver(algorithm),
        ).associateWith {
            OperatorStatistics(0.0, 1, Int.MAX_VALUE.toDouble())
        }.toMutableMap()

}