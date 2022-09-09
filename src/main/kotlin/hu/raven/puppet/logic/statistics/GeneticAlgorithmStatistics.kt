package hu.raven.puppet.logic.statistics

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.evolutionary.genetic.crossoveroperator.*

class GeneticAlgorithmStatistics<S : ISpecimenRepresentation> : AlgorithmStatistics {
    override var diversity = Double.MAX_VALUE
    val operatorsWithStatistics =
        listOf<CrossOverOperator<S>>(
            AlternatingEdgeCrossOver(),
            AlternatingPositionCrossOver(),
            CycleCrossOver(),
            DistancePreservingCrossOver(),
            GeneticEdgeRecombinationCrossOver(),
            HeuristicCrossOver(),
            MaximalPreservationCrossOver(),
            OrderBasedCrossOver(),
            OrderCrossOver(),
            PartiallyMatchedCrossOver(),
            PositionBasedCrossOver(),
            //broken SortedMatchCrossOver,
            SubTourChunksCrossOver(),
            VotingRecombinationCrossOver(),
        ).associateWith {
            OperatorStatistics(0.0, 1, Int.MAX_VALUE.toDouble())
        }.toMutableMap()

}