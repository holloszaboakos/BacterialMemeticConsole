package hu.raven.puppet.logic.state

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation

class IterativeAlgorithmStateWithMultipleCandidates<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    IterativeAlgorithmState {
    override var iteration = 0
    var population: MutableList<S> = mutableListOf()
    var copyOfBest: S? = null
    var copyOfWorst: S? = null
}