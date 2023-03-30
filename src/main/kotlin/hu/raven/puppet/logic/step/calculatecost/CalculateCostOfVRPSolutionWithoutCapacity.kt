package hu.raven.puppet.logic.step.calculatecost

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.model.physics.Euro
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.AlgorithmState
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.model.task.CostGraphEdge
import hu.raven.puppet.model.task.CostGraphVertex
import hu.raven.puppet.model.task.TransportUnit
import hu.raven.puppet.utility.extention.getEdgeBetween


class CalculateCostOfVRPSolutionWithoutCapacity<S : SolutionRepresentation<Euro>>(
    override val logger: DoubleLogger,
    override val subSolutionFactory: SolutionRepresentationFactory<S, Euro>,
    override val statistics: BacterialAlgorithmStatistics,
    override val algorithmState: AlgorithmState
) :
    CalculateCost<S, Euro>() {

    override operator fun invoke(
        specimen: SolutionRepresentation<Euro>
    ) {
        statistics.fitnessCallCount++
        algorithmState.run {
            var sumCost = Euro(0L)
            var geneIndex = 0
            specimen.forEachSliceIndexed { sliceIndex, slice ->
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
    }

    private fun calcCostOnEdge(salesman: TransportUnit, edge: CostGraphEdge) =
        salesman.fuelPrice * salesman.fuelConsumption * edge.length +
                salesman.salary * (edge.length / salesman.vehicleSpeed)

    private fun calcCostOnNode(salesman: TransportUnit, objective: CostGraphVertex) =
        salesman.salary * objective.time
}