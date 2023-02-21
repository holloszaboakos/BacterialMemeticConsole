package hu.raven.puppet.logic.step.evolutionary.bacterial.selectsegment

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.modules.AlgorithmParameters
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

sealed class SelectSegment<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> {
    val cloneSegmentLength: Int by KoinJavaComponent.inject(
        Int::class.java,
        named(AlgorithmParameters.CLONE_SEGMENT_LENGTH)
    )

    abstract operator fun invoke(
        specimen: S
    ): IntArray
}