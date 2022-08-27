package hu.raven.puppet.logic.localsearch

import hu.raven.puppet.logic.AAlgorithm4VRP
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.inner.setup.LocalSearchSetup
import org.koin.java.KoinJavaComponent.inject

sealed class SLocalSearch<S : ISpecimenRepresentation> : AAlgorithm4VRP<S>() {
    override val setup: LocalSearchSetup by inject(LocalSearchSetup::class.java)
    var iteration = 0
    lateinit var actualInstance: S

    fun iterate() = setup.iteration(this)
    fun initializeLocalSearch() = setup.initializeLocalSearch(this)
}