package hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class AlternatingPositionCrossOver : CrossOverOperator {
    override fun <S : ISpecimenRepresentation> invoke(
        parents: Pair<S, S>,
        child: S,
        algorithm: GeneticAlgorithm<S>
    ) {
        val parentsL = listOf(parents.first, parents.second)
        val childContains = BooleanArray(child.permutationSize) { false }
        child.setEach { _, _ -> child.permutationSize }

        var counter = 0
        (0 until child.permutationSize).forEach { geneIndex ->
            parentsL.forEach { parent ->
                if (!childContains[parent[geneIndex]]) {
                    child[counter] = parent[geneIndex]
                    childContains[child[counter]] = true
                    counter++
                }
            }
        }

        child.iteration = algorithm.iteration
        child.costCalculated = false
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen!")

    }
}