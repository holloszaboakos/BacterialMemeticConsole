package hu.raven.puppet.logic.step.common.calculatecost

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.extention.getEdgeBetween

class CalculateCostOfCVRPSolutionWithCapacity<S : ISpecimenRepresentation> : CalculateCost<S>() {
    override fun invoke(specimen: ISpecimenRepresentation) {
        statistics.fitnessCallCount++
        taskHolder.run {
            var sumCost = 0.0
            var geneIndex = 0
            specimen.forEachSliceIndexed { sliceIndex, slice ->
                val salesman = task.salesmen[sliceIndex]
                var cost = salesman.basePrice_Euro
                slice.map { it }.forEachIndexed { index, value ->
                    cost += when (index) {
                        0 -> {
                            val fromCenterEdge = task.costGraph.edgesFromCenter[value]
                            fromCenterEdge.length_Meter.toDouble() / 1000.0
                        }
                        geneIndex + slice.size - 1 -> {
                            val betweenEdge = task.costGraph.getEdgeBetween(slice[index - 1], value)
                            val toCenterEdge = task.costGraph.edgesToCenter[value]
                            (betweenEdge.length_Meter + toCenterEdge.length_Meter).toDouble() / 1000.0
                        }
                        else -> {
                            val betweenEdge = task.costGraph.getEdgeBetween(slice[index - 1], value)
                            betweenEdge.length_Meter.toDouble() / 1000.0
                        }
                    }

                }
                geneIndex += slice.size
                sumCost += cost

            }
            specimen.cost = sumCost
            specimen.costCalculated = true
            if (sumCost == 0.0) {
                println("Impossible!")
            }
        }
    }
}