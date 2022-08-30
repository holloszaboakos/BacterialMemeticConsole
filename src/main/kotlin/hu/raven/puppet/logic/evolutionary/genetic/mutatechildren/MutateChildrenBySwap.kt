package hu.raven.puppet.logic.evolutionary.genetic.mutatechildren

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.extention.slice
import kotlin.random.Random

class MutateChildrenBySwap<S : ISpecimenRepresentation>(
    override val algorithm: GeneticAlgorithm<S>
) : MutateChildren<S> {

    override fun invoke() {
        if (algorithm.task.costGraph.objectives.size > 1)
            algorithm.population.asSequence()
                .filter { it.iteration == algorithm.iteration }
                .shuffled()
                .slice(0 until algorithm.population.size / 4)
                .forEach { child ->
                    val firstCutIndex = Random.nextInt(algorithm.task.costGraph.objectives.size)
                    val secondCutIndex = Random.nextInt(algorithm.task.costGraph.objectives.size)
                        .let {
                            if (it == firstCutIndex)
                                (it + 1) % algorithm.task.costGraph.objectives.size
                            else
                                it
                        }

                    val tmp = child[firstCutIndex]
                    child[firstCutIndex] = child[secondCutIndex]
                    child[secondCutIndex] = tmp
                    if (!child.checkFormat())
                        throw Error("Invalid specimen!")
                }

    }
}