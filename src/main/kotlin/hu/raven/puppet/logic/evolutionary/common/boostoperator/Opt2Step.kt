package hu.raven.puppet.logic.evolutionary.common.boostoperator

import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import org.koin.java.KoinJavaComponent

class Opt2Step<S : ISpecimenRepresentation>(
    override val algorithm: SEvolutionaryAlgorithm<S>
) : BoostOperator<S> {

    val calculateCostOf: CalculateCost<S> by KoinJavaComponent.inject(CalculateCost::class.java)

    override fun invoke(specimen: S) {
        val bestCost = specimen.cost

        for (firstIndex in 0 until algorithm.population.first().permutationSize - 1) {
            for (secondIndex in firstIndex + 1 until algorithm.population.first().permutationSize) {
                specimen.swapGenes(firstIndex, secondIndex)
                calculateCostOf(specimen)

                if (specimen.cost >= bestCost) {
                    specimen.swapGenes(firstIndex, secondIndex)
                    specimen.cost = bestCost
                    continue
                }

                return
            }
        }
    }
}