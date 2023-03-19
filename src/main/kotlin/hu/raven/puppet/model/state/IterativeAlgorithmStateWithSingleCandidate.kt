package hu.raven.puppet.model.state

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation

class IterativeAlgorithmStateWithSingleCandidate<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    IterativeAlgorithmState {
    lateinit var actualCandidate: S
    override var iteration = 0
}