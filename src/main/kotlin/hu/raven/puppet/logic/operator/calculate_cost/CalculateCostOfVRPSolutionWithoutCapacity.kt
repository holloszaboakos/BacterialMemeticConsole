package hu.raven.puppet.logic.operator.calculate_cost


import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray.Companion.size
import hu.akos.hollo.szabo.math.vector.FloatVector
import hu.akos.hollo.szabo.physics.Euro
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.task.CostGraphEdge
import hu.raven.puppet.model.task.CostGraphVertex
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.model.task.TransportUnit
import hu.raven.puppet.utility.extention.getEdgeBetween


class CalculateCostOfVRPSolutionWithoutCapacity(
    override val task: Task
) : CalculateCost() {

    override operator fun invoke(solution: OnePartRepresentation): FloatVector {
        var sumCost = Euro(0)
        var geneIndex = 0
        solution.permutation
            .sliced { it >= task.costGraph.objectives.size - 1 }
            .forEachIndexed { sliceIndex, slice ->
                val salesman = task.transportUnits[sliceIndex]
                var cost = salesman.basePrice
                slice.map { it }.forEachIndexed { index, value ->
                    cost += when (index) {
                        0 -> {
                            if (slice.size != 1) {
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
        return sumCost.value.let { FloatVector.floatVectorOf(it) }
    }

    private fun calcCostOnEdge(salesman: TransportUnit, edge: CostGraphEdge) =
        salesman.fuelPrice * salesman.fuelConsumption * edge.length +
                salesman.salary * (edge.length / salesman.vehicleSpeed)

    private fun calcCostOnNode(salesman: TransportUnit, objective: CostGraphVertex) =
        salesman.salary * objective.time
}