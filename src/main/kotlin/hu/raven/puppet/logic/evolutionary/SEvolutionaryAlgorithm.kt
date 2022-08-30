package hu.raven.puppet.logic.evolutionary

import hu.raven.puppet.logic.AAlgorithm4VRP
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed class SEvolutionaryAlgorithm<S : ISpecimenRepresentation>(
    val iterationLimit: Int,
    val sizeOfPopulation: Int
) : AAlgorithm4VRP<S>() {

    var iteration = 0
    var population: ArrayList<S> = arrayListOf()
    var copyOfBest: S? = null
    var copyOfWorst: S? = null
}