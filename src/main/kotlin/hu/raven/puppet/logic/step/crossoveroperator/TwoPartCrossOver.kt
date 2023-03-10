package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.physics.PhysicsUnit

class TwoPartCrossOver<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : CrossOverOperator<S, C>() {

    override fun invoke(
        parents: Pair<S, S>,
        child: S,
    ) {
        TODO("Not yet implemented")
    }
}