package hu.raven.puppet.model.solution.factory

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.TwoPartRepresentation

class TwoPartRepresentationFactory<C : PhysicsUnit<C>> :
    SolutionRepresentationFactory<TwoPartRepresentation<C>, C>() {
    override fun produce(id: Int, values: Array<IntArray>): TwoPartRepresentation<C> =
        TwoPartRepresentation(id, values)

    override fun copy(specimen: TwoPartRepresentation<C>) = TwoPartRepresentation(specimen)
}