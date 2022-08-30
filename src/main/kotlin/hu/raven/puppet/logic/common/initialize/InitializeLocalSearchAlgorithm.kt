package hu.raven.puppet.logic.common.initialize

import hu.raven.puppet.logic.AAlgorithm4VRP
import hu.raven.puppet.logic.localsearch.SLocalSearch
import hu.raven.puppet.logic.localsearch.initialize.InitializeLocalSearch
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.runIfInstanceOf
import org.koin.java.KoinJavaComponent.inject

class InitializeLocalSearchAlgorithm<S : ISpecimenRepresentation>(
    override val algorithm: AAlgorithm4VRP<S>
) : InitializeAlgorithm<S> {
    val initializeLocalSearch: InitializeLocalSearch<S> by inject(InitializeLocalSearch::class.java)

    override fun invoke() =
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