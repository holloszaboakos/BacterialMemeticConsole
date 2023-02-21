package hu.raven.puppet.logic.step.localsearch

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.state.IterativeAlgorithmState
import hu.raven.puppet.logic.step.AlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.inject

abstract class LocalSearchStep<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : AlgorithmStep<S, C>() {
    protected val algorithmState: IterativeAlgorithmState<S, C> by inject()
}