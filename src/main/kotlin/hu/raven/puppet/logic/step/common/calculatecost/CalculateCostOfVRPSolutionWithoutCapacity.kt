package hu.raven.puppet.logic.step.common.calculatecost

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.task.DSalesman
import hu.raven.puppet.model.task.graph.DEdge
import hu.raven.puppet.model.task.graph.DObjective
import hu.raven.puppet.utility.extention.getEdgeBetween


class CalculateCostOfVRPSolutionWithoutCapacity<S : ISpecimenRepresentation> : CalculateCost<S>() {

    override operator fun invoke(
        specimen: ISpecimenRepresentation
    ) {
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
                            if (index != slice.size - 1) {
                                val fromCenterEdge = task.costGraph.edgesFromCenter[value]
                                val objective = task.costGraph.objectives[value]

                                calcCostOnEdge(salesman, fromCenterEdge) +
                                        calcCostOnNode(salesman, objective)
                            } else {
                                val fromCenterEdge = task.costGraph.edgesFromCenter[value]
                                val objective = task.costGraph.objectives[value]
                                val toCenterEdge = task.costGraph.edgesToCenter[value]

                                calcCostOnEdge(salesman, fromCenterEdge) +
                                        calcCostOnNode(salesman, objective) +
                                        calcCostOnEdge(salesman, toCenterEdge)

                            }
                        }
                        slice.size - 1 -> {
                            val betweenEdge = task.costGraph.getEdgeBetween(slice[index - 1], value)
                            val objective = task.costGraph.objectives[value]
                            val toCenterEdge = task.costGraph.edgesToCenter[value]

                            calcCostOnEdge(salesman, betweenEdge) +
                                    calcCostOnNode(salesman, objective) +
                                    calcCostOnEdge(salesman, toCenterEdge)
                        }
                        else -> {
                            val betweenEdge = task.costGraph.getEdgeBetween(slice[index - 1], value)
                            val objective = task.costGraph.objectives[value]

                            calcCostOnEdge(salesman, betweenEdge) +
                                    calcCostOnNode(salesman, objective)
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

    private fun calcCostOnEdge(salesman: DSalesman, edge: DEdge) =
        salesman.fuelPrice_EuroPerLiter * salesman.fuelConsuption_LiterPerMeter * edge.length_Meter +
                salesman.payment_EuroPerSecond * edge.length_Meter / salesman.vechicleSpeed_MeterPerSecond

    private fun calcCostOnNode(salesman: DSalesman, objective: DObjective) =
        salesman.payment_EuroPerSecond * objective.time_Second
}