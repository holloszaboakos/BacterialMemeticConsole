package hu.raven.puppet.logic.statistics

import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.logic.step.crossoveroperator.*
import hu.raven.puppet.model.physics.PhysicsUnit

class GeneticAlgorithmStatistics<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : AlgorithmStatistics {
    override var diversity = Double.MAX_VALUE
    val operatorsWithStatistics =
        listOf<CrossOverOperator<S, C>>(
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