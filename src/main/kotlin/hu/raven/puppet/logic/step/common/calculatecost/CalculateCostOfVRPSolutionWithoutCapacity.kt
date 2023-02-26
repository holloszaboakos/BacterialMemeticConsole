package hu.raven.puppet.logic.step.common.calculatecost

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.physics.Euro
import hu.raven.puppet.model.task.DSalesman
import hu.raven.puppet.model.task.graph.DEdge
import hu.raven.puppet.model.task.graph.DObjective
import hu.raven.puppet.utility.extention.getEdgeBetween


class CalculateCostOfVRPSolutionWithoutCapacity<S : ISpecimenRepresentation<Euro>> :
    CalculateCost<S, Euro>() {

    override operator fun invoke(
        specimen: ISpecimenRepresentation<Euro>
    ) {
        statistics.fitnessCallCount++
        taskHolder.run {
            var sumCost = Euro(0)
            var geneIndex = 0
            specimen.forEachSliceIndexed { sliceIndex, slice ->
                val salesman = task.salesmen[sliceIndex]
                var cost = salesman.basePrice
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
            if (sumCost == Euro(0)) {
                println("Impossible!")
            }
        }
    }

    private fun calcCostOnEdge(salesman: DSalesman, edge: DEdge) =
        salesman.fuelPrice * salesman.fuelConsumption * edge.length +
                salesman.salary * (edge.length / salesman.vehicleSpeed)

    private fun calcCostOnNode(salesman: DSalesman, objective: DObjective) =
        salesman.salary * objective.time
}