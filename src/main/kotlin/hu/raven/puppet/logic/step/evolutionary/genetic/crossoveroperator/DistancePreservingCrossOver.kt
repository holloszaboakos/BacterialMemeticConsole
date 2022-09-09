package hu.raven.puppet.logic.step.evolutionary.genetic.crossoveroperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class DistancePreservingCrossOver<S : ISpecimenRepresentation> : CrossOverOperator<S>() {

    override fun invoke(
        parents: Pair<S, S>,
        child: S,
    ) {
        val primaryInverse = parents.first.inverseOfPermutation()
        child.setEach { index, _ ->
            if (parents.first[index] == parents.second[index])
                parents.first[index]
            else
                -1
        }
        child.setEach { index, value ->
            if (value == -1)
                parents.second[primaryInverse[parents.second[index]]]
            else
                value
        }
        child.iteration = algorithmState.iteration
        child.costCalculated = false
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen!")

    }
}