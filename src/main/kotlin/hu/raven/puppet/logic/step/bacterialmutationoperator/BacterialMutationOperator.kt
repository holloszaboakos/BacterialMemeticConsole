package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment

sealed class BacterialMutationOperator<C : PhysicsUnit<C>> :
    EvolutionaryAlgorithmStep<C>() {
    abstract override val parameters: BacterialMutationParameterProvider<C>

    abstract operator fun invoke(
        clone: OnePartRepresentation<C>,
        selectedSegment: Segment,
    )
}