package hu.raven.puppet.logic.step

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithSingleCandidate
import hu.raven.puppet.utility.inject

abstract class IterativeAlgorithmStep<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : AlgorithmStep<S, C>() {
    override val algorithmState: IterativeAlgorithmStateWithSingleCandidate<S, C> by inject()
}