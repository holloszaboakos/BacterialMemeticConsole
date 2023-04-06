package hu.raven.puppet.logic.step.localsearchiteration

import hu.raven.puppet.logic.step.IterativeAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit

sealed class LocalSearchIteration<C : PhysicsUnit<C>> : IterativeAlgorithmStep<C>() {

    abstract operator fun invoke()
}