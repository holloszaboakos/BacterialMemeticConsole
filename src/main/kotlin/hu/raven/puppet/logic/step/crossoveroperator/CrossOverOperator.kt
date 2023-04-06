package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

sealed class CrossOverOperator<C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<C>() {
    companion object {
        fun <C : PhysicsUnit<C>> getVariants(
            logger: DoubleLogger,
            algorithmState: EvolutionaryAlgorithmState<C>,
            parameters: EvolutionaryAlgorithmParameterProvider<C>,
        ) = listOf<CrossOverOperator<C>>(
            AlternatingEdgeCrossOver(
                algorithmState,
                parameters,
            ),
            AlternatingPositionCrossOver(
                algorithmState,
                parameters
            ),
            CycleCrossOver(
                algorithmState,
                parameters
            ),
            DistancePreservingCrossOver(
                algorithmState,
                parameters
            ),
            GeneticEdgeRecombinationCrossOver(
                algorithmState,
                parameters,
            ),
            HeuristicCrossOver(
                algorithmState,
                parameters,
                logger
            ),
            MaximalPreservationCrossOver(
                algorithmState,
                parameters,
            ),
            OrderBasedCrossOver(
                algorithmState,
                parameters,
            ),
            OrderCrossOver(
                algorithmState,
                parameters,
            ),
            PartiallyMatchedCrossOver(
                algorithmState,
                parameters,
            ),
            PositionBasedCrossOver(
                algorithmState,
                parameters,
            ),
            //broken SortedMatchCrossOver,
            SubTourChunksCrossOver(
                algorithmState,
                parameters,
            ),
            VotingRecombinationCrossOver(
                algorithmState,
                parameters,
            ),
        )
    }

    abstract operator fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
    )
}