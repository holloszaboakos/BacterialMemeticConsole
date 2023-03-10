package hu.raven.puppet.logic.state

import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.physics.PhysicsUnit

class IterativeAlgorithmStateWithSingleCandidate<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    IterativeAlgorithmState {
    lateinit var actualCandidate: S
    override var iteration = 0
}