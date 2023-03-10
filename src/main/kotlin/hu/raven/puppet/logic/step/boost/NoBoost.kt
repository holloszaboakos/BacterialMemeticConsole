package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation

class NoBoost<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : Boost<S, C>() {
    override suspend operator fun invoke() {
    }
}