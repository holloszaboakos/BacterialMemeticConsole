package hu.raven.puppet.logic.step.mutatechildren

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates

class MutateChildrenByReverseOrReset<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<S, C>,
) : MutateChildren<S, C>() {
    val mutateChildrenByReset = MutateChildrenByReset(
        logger, subSolutionFactory, algorithmState, parameters
    )
    val mutateChildrenByReverse = MutateChildrenByReverse(
        logger, subSolutionFactory, algorithmState, parameters
    )

    override fun invoke() {
        if (algorithmState.iteration % 100 == 0) {
            mutateChildrenByReset()
            return
        }

        mutateChildrenByReverse()
    }
}