package hu.raven.puppet.logic.localsearch

import hu.raven.puppet.logic.AAlgorithm4VRP
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed class SLocalSearch<S : ISpecimenRepresentation> : AAlgorithm4VRP<S>() {
    var iteration = 0
    lateinit var actualInstance: S
}