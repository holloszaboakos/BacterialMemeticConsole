package hu.raven.puppet.logic.common.initialize

import hu.raven.puppet.logic.AAlgorithm4VRP
import hu.raven.puppet.logic.localsearch.SLocalSearch
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.runIfInstanceOf

class InitializeLocalSearchAlgorithm : InitializeAlgorithm {
    override fun <S : ISpecimenRepresentation> invoke(algorithm: AAlgorithm4VRP<S>) =
        algorithm.runIfInstanceOf<SLocalSearch<S>> {
            when (state) {
                AAlgorithm4VRP.State.CREATED -> {
                    initializeLocalSearch()
                    state = AAlgorithm4VRP.State.INITIALIZED
                }
                else -> {
                }
            }
        }
}