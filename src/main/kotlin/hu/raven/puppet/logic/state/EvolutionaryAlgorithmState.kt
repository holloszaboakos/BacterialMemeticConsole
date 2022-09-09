package hu.raven.puppet.logic.state

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class EvolutionaryAlgorithmState<S : ISpecimenRepresentation> : AlgorithmState {
    var iteration = 0
    var population: MutableList<S> = mutableListOf()
    var copyOfBest: S? = null
    var copyOfWorst: S? = null
}