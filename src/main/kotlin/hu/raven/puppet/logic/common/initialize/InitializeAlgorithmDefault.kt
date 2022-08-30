package hu.raven.puppet.logic.common.initialize

import hu.raven.puppet.logic.AAlgorithm4VRP
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class InitializeAlgorithmDefault<S : ISpecimenRepresentation>(
    override val algorithm: AAlgorithm4VRP<S>
) : InitializeAlgorithm<S> {

    override operator fun invoke() =
        algorithm.run {
            when (state) {
                AAlgorithm4VRP.State.CREATED -> {
                    state = AAlgorithm4VRP.State.INITIALIZED
                }
                else -> {
                }
            }
        }
}