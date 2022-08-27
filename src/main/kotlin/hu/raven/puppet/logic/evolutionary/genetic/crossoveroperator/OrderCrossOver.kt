package hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlin.random.Random

class OrderCrossOver : CrossOverOperator {
    override fun <S : ISpecimenRepresentation> invoke(
        parents: Pair<S, S>,
        child: S,
        algorithm: GeneticAlgorithm<S>
    ) {
        val parentsL = parents.toList()
        val cut = arrayOf(Random.nextInt(parentsL.size), Random.nextInt(parentsL.size - 1))
        if (cut[0] == cut[1])
            cut[1]++
        cut.sort()

        val primerParent = parents.first
        val seconderParent = parents.second
        val seconderCopy = seconderParent.copyOfPermutationBy(::MutableList) as MutableList
        val seconderInverse = seconderParent.inverseOfPermutation()

        //clean child
        //copy parent middle to child
        child.setEach { index, _ ->
            if (index in cut[0]..cut[1]) {
                seconderCopy[seconderInverse[primerParent[index]]] = child.permutationSize
                primerParent[index]
            } else
                child.permutationSize
        }
        seconderCopy.removeIf { it == child.permutationSize }
        //fill missing places of child
        var counter = -1
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