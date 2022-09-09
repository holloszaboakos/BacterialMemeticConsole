package hu.raven.puppet.logic.step.evolutionary.genetic.crossoveroperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlin.random.Random

class PartiallyMatchedCrossOver<S : ISpecimenRepresentation> : CrossOverOperator<S>() {

    override fun invoke(
        parents: Pair<S, S>,
        child: S,
    ) {
        val cut = arrayOf(
            Random.nextInt(parents.first.permutationIndices.count()),
            Random.nextInt(parents.first.permutationIndices.count() - 1)
        )
        if (cut[0] == cut[1])
            cut[1]++
        cut.sort()
        val primerParent = parents.first
        val seconderParent = parents.second
        val seconderCopy = seconderParent.copyOfPermutationBy(::MutableList) as MutableList
        val seconderInverse = seconderParent.inverseOfPermutation()

        //copy parent middle to child
        //start mapping
        child.setEach { index, _ ->
            if (index in cut[0]..cut[1])
                child.permutationSize
            else {
                seconderCopy[seconderInverse[primerParent[index]]] = child.permutationSize
                primerParent[index]
            }
        }
        seconderCopy.removeIf { it == child.permutationSize }
        //fill empty positions
        seconderCopy.forEachIndexed { index, value ->
            child[cut[0] + index] = value
        }

        child.iteration = algorithmState.iteration
        child.costCalculated = false
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen!")
    }
}