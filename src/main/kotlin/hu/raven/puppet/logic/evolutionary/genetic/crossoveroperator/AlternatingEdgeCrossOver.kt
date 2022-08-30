package hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class AlternatingEdgeCrossOver<S : ISpecimenRepresentation>(
    override val algorithm: GeneticAlgorithm<S>
) : CrossOverOperator<S> {

    override fun invoke(
        parents: Pair<S, S>,
        child: S,
    ) {
        val childContains = Array(child.permutationSize) { false }
        val randomPermutation = IntArray(child.permutationSize) { it }
        randomPermutation.shuffle()
        var lastIndex = 0
        val parentsL = listOf(parents.first, parents.second)
        val parentsNeighbouring = List(2) { parentIndex ->
            parentsL[parentIndex].sequentialOfPermutation()
        }
        child.setEach { _, _ -> child.permutationSize }
        child[0] = (0 until child.permutationSize).random()
        childContains[child[0]] = true
        (1 until child.permutationSize).forEach { geneIndex ->
            val parent = parentsNeighbouring[geneIndex % 2]

            if (!childContains[parent[child[geneIndex - 1]]])
                child[geneIndex] = parent[child[geneIndex - 1]]
            else {
                for (index in lastIndex until randomPermutation.size) {
                    if (!childContains[randomPermutation[index]]) {
                        child[geneIndex] = randomPermutation[index]
                        lastIndex = index + 1
                        break
                    }
                }
            }
            childContains[child[geneIndex]] = true
        }

        child.iteration = algorithm.iteration
        child.costCalculated = false
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen!")


    }
}