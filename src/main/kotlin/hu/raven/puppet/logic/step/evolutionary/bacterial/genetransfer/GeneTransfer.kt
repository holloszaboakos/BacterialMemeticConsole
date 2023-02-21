package hu.raven.puppet.logic.step.evolutionary.bacterial.genetransfer

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.evolutionary.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.modules.AlgorithmParameters
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

sealed class GeneTransfer<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<S, C>() {
    val injectionCount: Int by KoinJavaComponent.inject(Int::class.java, named(AlgorithmParameters.INJECTION_COUNT))

    abstract suspend operator fun invoke()
}
