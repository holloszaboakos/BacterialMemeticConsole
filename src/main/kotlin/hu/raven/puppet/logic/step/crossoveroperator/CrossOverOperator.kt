package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates

sealed class CrossOverOperator<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<S, C>() {
    companion object {
        fun <S : SolutionRepresentation<C>, C : PhysicsUnit<C>> getVariants(
            logger: DoubleLogger,
            taskHolder: VRPTaskHolder,
            subSolutionFactory: SolutionRepresentationFactory<S, C>,
            algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
            sizeOfPopulation: Int,
            iterationLimit: Int,
            geneCount: Int
        ) = listOf<CrossOverOperator<S, C>>(
            AlternatingEdgeCrossOver(
                logger,
                taskHolder,
                subSolutionFactory,
                algorithmState,
                sizeOfPopulation,
                iterationLimit,
                geneCount
            ),
            AlternatingPositionCrossOver(
                logger,
                taskHolder,
                subSolutionFactory,
                algorithmState,
                sizeOfPopulation,
                iterationLimit,
                geneCount
            ),
            CycleCrossOver(
                logger,
                taskHolder,
                subSolutionFactory,
                algorithmState,
                sizeOfPopulation,
                iterationLimit,
                geneCount
            ),
            DistancePreservingCrossOver(
                logger,
                taskHolder,
                subSolutionFactory,
                algorithmState,
                sizeOfPopulation,
                iterationLimit,
                geneCount
            ),
            GeneticEdgeRecombinationCrossOver(
                logger,
                taskHolder,
                subSolutionFactory,
                algorithmState,
                sizeOfPopulation,
                iterationLimit,
                geneCount
            ),
            HeuristicCrossOver(
                logger,
                taskHolder,
                subSolutionFactory,
                algorithmState,
                sizeOfPopulation,
                iterationLimit,
                geneCount
            ),
            MaximalPreservationCrossOver(
                logger,
                taskHolder,
                subSolutionFactory,
                algorithmState,
                sizeOfPopulation,
                iterationLimit,
                geneCount
            ),
            OrderBasedCrossOver(
                logger,
                taskHolder,
                subSolutionFactory,
                algorithmState,
                sizeOfPopulation,
                iterationLimit,
                geneCount
            ),
            OrderCrossOver(
                logger,
                taskHolder,
                subSolutionFactory,
                algorithmState,
                sizeOfPopulation,
                iterationLimit,
                geneCount
            ),
            PartiallyMatchedCrossOver(
                logger,
                taskHolder,
                subSolutionFactory,
                algorithmState,
                sizeOfPopulation,
                iterationLimit,
                geneCount
            ),
            PositionBasedCrossOver(
                logger,
                taskHolder,
                subSolutionFactory,
                algorithmState,
                sizeOfPopulation,
                iterationLimit,
                geneCount
            ),
            //broken SortedMatchCrossOver,
            SubTourChunksCrossOver(
                logger,
                taskHolder,
                subSolutionFactory,
                algorithmState,
                sizeOfPopulation,
                iterationLimit,
                geneCount
            ),
            VotingRecombinationCrossOver(
                logger,
                taskHolder,
                subSolutionFactory,
                algorithmState,
                sizeOfPopulation,
                iterationLimit,
                geneCount
            ),
        )
    }

    abstract operator fun invoke(
        parents: Pair<S, S>,
        child: S
    )
}