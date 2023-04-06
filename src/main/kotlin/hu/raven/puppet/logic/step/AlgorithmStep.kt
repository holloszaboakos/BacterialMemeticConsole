package hu.raven.puppet.logic.step

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.AlgorithmState

abstract class AlgorithmStep<C : PhysicsUnit<C>> {
    protected abstract val algorithmState: AlgorithmState
}