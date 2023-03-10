package hu.raven.puppet.model.solution.factory

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation

class OnePartRepresentationFactory<C : PhysicsUnit<C>> :
    SolutionRepresentationFactory<OnePartRepresentation<C>, C>() {
    override fun produce(id: Int, values: Array<IntArray>): OnePartRepresentation<C> =
        OnePartRepresentation(id, values)

    override fun copy(specimen: OnePartRepresentation<C>) = OnePartRepresentation(specimen)
}