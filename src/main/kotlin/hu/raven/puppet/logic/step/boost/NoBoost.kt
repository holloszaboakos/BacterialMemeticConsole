package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.physics.PhysicsUnit

class NoBoost<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : hu.raven.puppet.logic.step.boost.Boost<S, C>() {
    override suspend operator fun invoke() {
    }
}