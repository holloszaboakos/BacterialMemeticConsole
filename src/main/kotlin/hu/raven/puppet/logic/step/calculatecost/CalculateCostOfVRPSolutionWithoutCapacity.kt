package hu.raven.puppet.logic.step.calculatecost

import hu.raven.puppet.model.physics.Euro
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.task.CostGraphEdge
import hu.raven.puppet.model.task.CostGraphVertex
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.model.task.TransportUnit
import hu.raven.puppet.utility.extention.getEdgeBetween


class CalculateCostOfVRPSolutionWithoutCapacity(
    override val task: Task
) : CalculateCost<Euro>() {

    override operator fun invoke(
        specimen: OnePartRepresentationWithIteration<Euro>
    ) {
        var sumCost = Euro(0L)
        var geneIndex = 0
        specimen.permutation
            .sliced { it >= task.costGraph.objectives.size - 1 }
            .forEachIndexed { sliceIndex, slice ->
                val salesman = task.transportUnits[sliceIndex]
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
        if (sumCost == Euro(0L)) {
            println("Impossible!")
        }
    }

    private fun calcCostOnEdge(salesman: TransportUnit, edge: CostGraphEdge) =
        salesman.fuelPrice * salesman.fuelConsumption * edge.length +
                salesman.salary * (edge.length / salesman.vehicleSpeed)

    private fun calcCostOnNode(salesman: TransportUnit, objective: CostGraphVertex) =
        salesman.salary * objective.time
}