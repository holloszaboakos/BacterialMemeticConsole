package hu.raven.puppet.logic.evolutionary.common.boostoperator

import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import org.koin.java.KoinJavaComponent.inject

class Opt2<S : ISpecimenRepresentation>(
    override val algorithm: SEvolutionaryAlgorithm<S>
) : BoostOperator<S> {

    val calculateCostOf: CalculateCost<S> by inject(CalculateCost::class.java)

    override fun invoke(specimen: S) {
        var improved = true
        var bestCost = specimen.cost

        while (improved) {
            improved = false
            for (firstIndex in 0 until algorithm.population.first().permutationSize - 1) {
                for (secondIndex in firstIndex + 1 until algorithm.population.first().permutationSize) {
                    specimen.swapGenes(firstIndex, secondIndex)
                    calculateCostOf(specimen)

                    if (specimen.cost >= bestCost) {
                        specimen.swapGenes(firstIndex, secondIndex)
                        specimen.cost = bestCost
                        continue
                    }

                    improved = true
                    bestCost = specimen.cost
                }
            }
        }
    }
}