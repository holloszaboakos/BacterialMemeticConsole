package hu.raven.puppet.logic.evolutionary.common.boostoperator

import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import org.koin.java.KoinJavaComponent

class Opt2CycleWithRandomOrder<S : ISpecimenRepresentation>(
    override val algorithm: SEvolutionaryAlgorithm<S>
) : BoostOperator<S> {

    val calculateCostOf: CalculateCost<S> by KoinJavaComponent.inject(CalculateCost::class.java)

    var shuffler = intArrayOf()

    override fun invoke(specimen: S) {
        if (shuffler.isEmpty()) {
            shuffler = (0 until algorithm.population.first().permutationSize)
                .shuffled()
                .toIntArray()
        }


        var bestCost = specimen.cost

        for (firstIndexIndex in 0 until algorithm.population.first().permutationSize - 1) {
            val firstIndex = shuffler[firstIndexIndex]
            for (secondIndexIndex in firstIndexIndex + 1 until algorithm.population.first().permutationSize) {
                val secondIndex = shuffler[secondIndexIndex]

                specimen.swapGenes(firstIndex, secondIndex)
                calculateCostOf(specimen)

                if (specimen.cost >= bestCost) {
                    specimen.swapGenes(firstIndex, secondIndex)
                    specimen.cost = bestCost
                    continue
                }

                bestCost = specimen.cost
            }
        }
    }
}