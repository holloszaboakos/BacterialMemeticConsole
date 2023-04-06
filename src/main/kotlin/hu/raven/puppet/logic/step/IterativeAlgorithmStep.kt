package hu.raven.puppet.logic.step

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.LocalSearchAlgorithmState
import hu.raven.puppet.utility.inject

abstract class IterativeAlgorithmStep<C : PhysicsUnit<C>> : AlgorithmStep<C>() {
    override val algorithmState: LocalSearchAlgorithmState<C> by inject()
}