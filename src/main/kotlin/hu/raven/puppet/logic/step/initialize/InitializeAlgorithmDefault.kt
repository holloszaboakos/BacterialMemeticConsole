package hu.raven.puppet.logic.step.initialize

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.AlgorithmState

class InitializeAlgorithmDefault<C : PhysicsUnit<C>>(
    override val algorithmState: AlgorithmState
) : InitializeAlgorithm<C>() {

    override operator fun invoke() {}
}