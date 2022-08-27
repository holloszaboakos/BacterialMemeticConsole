package hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlin.random.Random

class OrderBasedCrossOver : CrossOverOperator {
    override fun <S : ISpecimenRepresentation> invoke(
        parents: Pair<S, S>,
        child: S,
        algorithm: GeneticAlgorithm<S>
    ) {
        val primerParent = parents.first
        val seconderParent = parents.second
        val seconderCopy = seconderParent.copyOfPermutationBy(::MutableList) as MutableList
        val seconderInverse = seconderParent.inverseOfPermutation()

        //clean child
        //copy parent middle to child
        child.setEach { valueIndex, _ ->
            if (Random.nextBoolean()) {
                seconderCopy[seconderInverse[primerParent[valueIndex]]] = child.permutationSize
                primerParent[valueIndex]
            } else
                child.permutationSize
        }

        seconderCopy.removeIf { it == child.permutationSize }

        var counter = -1
        //fill missing places of child
        child.setEach { _, value ->
            if (value == child.permutationSize) {
                counter++
                seconderCopy[counter]
            } else
                value
        }
        child.iteration = algorithm.iteration
        child.costCalculated = false
        child.inUse = true

        if (!child.checkFormat())
            throw Error("Invalid specimen!")


    }
}