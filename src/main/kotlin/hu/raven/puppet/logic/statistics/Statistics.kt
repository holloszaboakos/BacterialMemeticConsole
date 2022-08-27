package hu.raven.puppet.logic.statistics

import hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator.*

class Statistics(
    val operatorsWithStatistics: MutableMap<CrossOverOperator, OperatorStatistics> =
        listOf(
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
        }.toMutableMap(),
    var fitnessCallCount: Long = 0,
    var mutationStepCall: Long = 0,
    var mutationCall: Long = 0,
    var mutationCycleCall: Long = 0,
    var mutationOperatorCall: Long = 0,
    var mutationImprovementCountOnBest: Long = 0,
    var mutationImprovementCountOnAll: Long = 0,
    var diversity: Double = Double.MAX_VALUE
)