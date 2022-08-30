package hu.raven.puppet.logic.evolutionary.genetic.mutatechildren

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.extention.slice
import kotlin.random.Random

class MutateChildrenBySwap : MutateChildren {
    override fun <S : ISpecimenRepresentation> invoke(algorithm: GeneticAlgorithm<S>) {
        if (algorithm.costGraph.objectives.size > 1)
            algorithm.population.asSequence()
                .filter { it.iteration == algorithm.iteration }
                .shuffled()
                .slice(0 until algorithm.population.size / 4)
                .forEach { child ->
                    val firstCutIndex = Random.nextInt(algorithm.costGraph.objectives.size)
                    val secondCutIndex = Random.nextInt(algorithm.costGraph.objectives.size)
                        .let {
                            if (it == firstCutIndex)
                                (it + 1) % algorithm.costGraph.objectives.size
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