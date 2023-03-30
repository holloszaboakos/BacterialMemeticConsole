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
                subSolutionFactory,
                algorithmState,
                parameters,
            ),
            AlternatingPositionCrossOver(
                subSolutionFactory,
                algorithmState,
                parameters
            ),
            CycleCrossOver(
                subSolutionFactory,
                algorithmState,
                parameters
            ),
            DistancePreservingCrossOver(
                subSolutionFactory,
                algorithmState,
                parameters
            ),
            GeneticEdgeRecombinationCrossOver(
                subSolutionFactory,
                algorithmState,
                parameters,
            ),
            HeuristicCrossOver(
                subSolutionFactory,
                algorithmState,
                parameters,
                logger
            ),
            MaximalPreservationCrossOver(
                subSolutionFactory,
                algorithmState,
                parameters,
            ),
            OrderBasedCrossOver(
                subSolutionFactory,
                algorithmState,
                parameters,
            ),
            OrderCrossOver(
                subSolutionFactory,
                algorithmState,
                parameters,
            ),
            PartiallyMatchedCrossOver(
                subSolutionFactory,
                algorithmState,
                parameters,
            ),
            PositionBasedCrossOver(
                subSolutionFactory,
                algorithmState,
                parameters,
            ),
            //broken SortedMatchCrossOver,
            SubTourChunksCrossOver(
                subSolutionFactory,
                algorithmState,
                parameters,
            ),
            VotingRecombinationCrossOver(
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