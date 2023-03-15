package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.modules.AlgorithmParameters
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

sealed class SelectSegment<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<S, C>() {
    val cloneSegmentLength: Int by KoinJavaComponent.inject(
        Int::class.java,
        named(AlgorithmParameters.CLONE_SEGMENT_LENGTH)
    )

    abstract operator fun invoke(
        specimen: S,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment
}