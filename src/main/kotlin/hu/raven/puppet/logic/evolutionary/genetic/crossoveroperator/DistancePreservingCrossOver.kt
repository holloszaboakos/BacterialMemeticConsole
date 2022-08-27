package hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class DistancePreservingCrossOver : CrossOverOperator {
    override fun <S : ISpecimenRepresentation> invoke(
        parents: Pair<S, S>,
        child: S,
        algorithm: GeneticAlgorithm<S>
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
        child.iteration = algorithm.iteration
        child.costCalculated = false
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen!")

    }
}