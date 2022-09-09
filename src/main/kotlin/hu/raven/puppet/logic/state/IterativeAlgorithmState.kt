package hu.raven.puppet.logic.state

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class IterativeAlgorithmState<S : ISpecimenRepresentation> : AlgorithmState {
    var iteration = 0
    lateinit var actualCandidate: S
}