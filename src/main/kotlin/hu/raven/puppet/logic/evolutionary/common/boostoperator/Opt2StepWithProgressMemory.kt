package hu.raven.puppet.logic.evolutionary.common.boostoperator

import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import org.koin.java.KoinJavaComponent.inject

class Opt2StepWithProgressMemory<S : ISpecimenRepresentation>(
    override val algorithm: SEvolutionaryAlgorithm<S>
) : BoostOperator<S> {
    val calculateCostOf: CalculateCost<*> by inject(CalculateCost::class.java)

    var lastPosition = Pair(0, 1)

    override fun invoke(specimen: S) {
        val bestCost = specimen.cost
        var improved = false

        outer@ for (firstIndex in lastPosition.first until algorithm.population.first().permutationSize - 1) {
            val secondIndexStart =
                if (firstIndex == lastPosition.first) lastPosition.second
                else firstIndex + 1
            for (secondIndex in secondIndexStart until algorithm.task.costGraph.objectives.size) {
                specimen.swapGenes(firstIndex, secondIndex)
                calculateCostOf(specimen)

                if (specimen.cost >= bestCost) {
                    specimen.swapGenes(firstIndex, secondIndex)
                    specimen.cost = bestCost
                    continue
                }

                improved = true
                lastPosition = Pair(firstIndex, secondIndex)
                break@outer
            }
        }

        if (!improved) {
            lastPosition = Pair(0, 1)
        }
    }
}