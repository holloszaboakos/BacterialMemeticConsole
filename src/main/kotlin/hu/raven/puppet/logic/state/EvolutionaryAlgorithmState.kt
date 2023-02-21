package hu.raven.puppet.logic.state

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.physics.PhysicsUnit

class EvolutionaryAlgorithmState<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : AlgorithmState {
    var iteration = 0
    var population: MutableList<S> = mutableListOf()
    var copyOfBest: S? = null
    var copyOfWorst: S? = null
}