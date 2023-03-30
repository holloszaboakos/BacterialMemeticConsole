package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates

sealed class CrossOverOperator<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<S, C>() {
    companion object {
        fun <S : SolutionRepresentation<C>, C : PhysicsUnit<C>> getVariants(
            logger: DoubleLogger,
            subSolutionFactory: SolutionRepresentationFactory<S, C>,
            algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
            parameters: EvolutionaryAlgorithmParameterProvider<S, C>,
        ) = listOf<CrossOverOperator<S, C>>(
            AlternatingEdgeCrossOver(
                logger,
                subSolutionFactory,
                algorithmState,
                parameters,
            ),
            AlternatingPositionCrossOver(
                logger,
                subSolutionFactory,
                algorithmState,
                parameters
            ),
            CycleCrossOver(
                logger,
                subSolutionFactory,
                algorithmState,
                parameters
            ),
            DistancePreservingCrossOver(
                logger,
                subSolutionFactory,
                algorithmState,
                parameters
            ),
            GeneticEdgeRecombinationCrossOver(
                logger,
                subSolutionFactory,
                algorithmState,
                parameters,
            ),
            HeuristicCrossOver(
                logger,
                subSolutionFactory,
                algorithmState,
                parameters,
            ),
            MaximalPreservationCrossOver(
                logger,
                subSolutionFactory,
                algorithmState,
                parameters,
            ),
            OrderBasedCrossOver(
                logger,
                subSolutionFactory,
                algorithmState,
                parameters,
            ),
            OrderCrossOver(
                logger,
                subSolutionFactory,
                algorithmState,
                parameters,
            ),
            PartiallyMatchedCrossOver(
                logger,
                subSolutionFactory,
                algorithmState,
                parameters,
            ),
            PositionBasedCrossOver(
                logger,
                subSolutionFactory,
                algorithmState,
                parameters,
            ),
            //broken SortedMatchCrossOver,
            SubTourChunksCrossOver(
                logger,
                subSolutionFactory,
                algorithmState,
                parameters,
            ),
            VotingRecombinationCrossOver(
                logger,
                subSolutionFactory,
                algorithmState,
                parameters,
            ),
        )
    }

    abstract operator fun invoke(
        parents: Pair<S, S>,
        child: S
    )
}