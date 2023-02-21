package hu.raven.puppet.logic.state

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.physics.PhysicsUnit

class IterativeAlgorithmState<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : AlgorithmState {
    var iteration = 0
    lateinit var actualCandidate: S
}