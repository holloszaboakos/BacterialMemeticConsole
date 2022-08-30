package hu.raven.puppet.logic.evolutionary.genetic.mutatechildren

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.extention.slice
import kotlin.random.Random

class MutateChildrenByReverse<S : ISpecimenRepresentation>(
    override val algorithm: GeneticAlgorithm<S>
) : MutateChildren<S> {

    override fun invoke() {
        if (algorithm.task.costGraph.objectives.size > 1)
            algorithm.population.asSequence()
                .filter { it.iteration == algorithm.iteration }
                .shuffled()
                .slice(0 until (algorithm.population.size / 4))
                .forEach { child ->
                    val firstCutIndex = Random.nextInt(algorithm.task.costGraph.objectives.size)
                    val secondCutIndex = Random.nextInt(algorithm.task.costGraph.objectives.size)
                        .let {
                            if (it == firstCutIndex)
                                (it + 1) % algorithm.task.costGraph.objectives.size
                            else
                                it
                        }

                    if (secondCutIndex > firstCutIndex) {
                        val reversed = child.slice(firstCutIndex..secondCutIndex).toList().reversed()
                        for (geneIndex in firstCutIndex..secondCutIndex)
                            child[geneIndex] = reversed[geneIndex - firstCutIndex]
                    } else {
                        val reversed = child.slice(secondCutIndex..firstCutIndex).toList().reversed()
                        for (geneIndex in secondCutIndex..firstCutIndex)
                            child[geneIndex] = reversed[geneIndex - secondCutIndex]
                    }
                    if (!child.checkFormat())
                        throw Error("Invalid specimen!")

                }
    }
}