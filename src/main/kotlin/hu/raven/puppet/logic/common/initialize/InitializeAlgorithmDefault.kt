package hu.raven.puppet.logic.common.initialize

import hu.raven.puppet.logic.AAlgorithm4VRP
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class InitializeAlgorithmDefault : InitializeAlgorithm {
    override operator fun <S : ISpecimenRepresentation> invoke(algorithm: AAlgorithm4VRP<S>) =
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