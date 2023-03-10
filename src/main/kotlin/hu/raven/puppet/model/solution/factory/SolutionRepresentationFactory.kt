package hu.raven.puppet.model.solution.factory

import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.physics.PhysicsUnit

sealed class SolutionRepresentationFactory<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> {
    abstract fun produce(id: Int, values: Array<IntArray>): S
    abstract fun copy(specimen: S): S
}
