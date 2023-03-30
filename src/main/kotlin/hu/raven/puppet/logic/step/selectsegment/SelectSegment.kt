package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.solution.SolutionRepresentation

sealed class SelectSegment<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<S, C>() {
    abstract override val parameters: BacterialMutationParameterProvider<S, C>

    abstract operator fun invoke(
        specimen: S,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment
}