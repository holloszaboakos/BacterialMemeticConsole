package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation

class DistancePreservingCrossOver<C : PhysicsUnit<C>>() : CrossOverOperator<C>() {

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
    ) {
        val primaryInverse = parents.first.permutation.inverse()
        child.permutation.setEach { index, _ ->
            if (parents.first.permutation[index] == parents.second.permutation[index])
                parents.first.permutation[index]
            else
                -1
        }
        child.permutation.setEach { index, value ->
            if (value == -1)
                parents.second.permutation[primaryInverse[parents.second.permutation[index]]]
            else
                value
        }
    }
}