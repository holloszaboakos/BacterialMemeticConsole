package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation

sealed class CrossOverOperator<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<S, C>() {
    companion object {
        fun <S : SolutionRepresentation<C>, C : PhysicsUnit<C>> getVariants() = listOf<CrossOverOperator<S, C>>(
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
        )
    }

    abstract operator fun invoke(
        parents: Pair<S, S>,
        child: S
    )
}