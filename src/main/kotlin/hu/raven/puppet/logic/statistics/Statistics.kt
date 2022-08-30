package hu.raven.puppet.logic.statistics

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator.*
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class Statistics<S : ISpecimenRepresentation>(
    val algorithm: GeneticAlgorithm<S>
) {
    val operatorsWithStatistics: MutableMap<CrossOverOperator<S>, OperatorStatistics> =
        listOf(
            AlternatingEdgeCrossOver<S>(algorithm),
            AlternatingPositionCrossOver<S>(algorithm),
            CycleCrossOver<S>(algorithm),
            DistancePreservingCrossOver<S>(algorithm),
            GeneticEdgeRecombinationCrossOver<S>(algorithm),
            HeuristicCrossOver<S>(algorithm),
            MaximalPreservationCrossOver<S>(algorithm),
            OrderBasedCrossOver<S>(algorithm),
            OrderCrossOver<S>(algorithm),
            PartiallyMatchedCrossOver<S>(algorithm),
            PositionBasedCrossOver<S>(algorithm),
            //broken SortedMatchCrossOver,
            SubTourChunksCrossOver<S>(algorithm),
            VotingRecombinationCrossOver<S>(algorithm),
        ).associateWith {
            OperatorStatistics(0.0, 1, Int.MAX_VALUE.toDouble())
        }.toMutableMap()

    var fitnessCallCount: Long = 0
    var mutationStepCall: Long = 0
    var mutationCall: Long = 0
    var mutationCycleCall: Long = 0
    var mutationOperatorCall: Long = 0
    var mutationImprovementCountOnBest: Long = 0
    var mutationImprovementCountOnAll: Long = 0
    var diversity: Double = Double.MAX_VALUE
}