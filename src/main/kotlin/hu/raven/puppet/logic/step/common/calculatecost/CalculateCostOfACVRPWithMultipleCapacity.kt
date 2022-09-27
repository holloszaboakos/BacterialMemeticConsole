package hu.raven.puppet.logic.step.common.calculatecost

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.task.DSalesman
import hu.raven.puppet.model.task.graph.DEdge
import hu.raven.puppet.model.task.graph.DObjective
import hu.raven.puppet.utility.extention.getEdgeBetween

class CalculateCostOfACVRPWithMultipleCapacity<S : ISpecimenRepresentation> : CalculateCost<S>() {

    private data class TakenCapacity(
        var volume: Double = 0.0,
        var weight: Double = 0.0,
        var time: Double = 0.0,
    )

    override operator fun invoke(
        specimen: ISpecimenRepresentation
    ) {
        statistics.fitnessCallCount++
        taskHolder.run {
            var sumCost = 0.0
            specimen.forEachSliceIndexed { sliceIndex, slice ->
                val salesman = task.salesmen[sliceIndex]
                var cost = salesman.basePrice_Euro
                val takenCapacity = TakenCapacity()

                slice.forEachIndexed { index, value ->
                    val toCenterEdge = task.costGraph.edgesToCenter[value]
                    val objective = task.costGraph.objectives[value]
                    val fromCenterEdge = task.costGraph.edgesFromCenter[value]

                    cost += when (index) {
                        0 -> {
                            val addedTimeWithReturn =
                                fromCenterEdge.length_Meter / salesman.vechicleSpeed_MeterPerSecond +
                                        objective.time_Second +
                                        toCenterEdge.length_Meter / salesman.vechicleSpeed_MeterPerSecond

                            when {
                                index != slice.size - 1 &&
                                        canSalesmanTakeObjective(
                                            takenCapacity,
                                            objective,
                                            addedTimeWithReturn,
                                            salesman
                                        ) -> {
                                    takenCapacity.volume += objective.volume_Stere
                                    takenCapacity.weight += objective.weight_Gramm
                                    takenCapacity.time += fromCenterEdge.length_Meter / salesman.vechicleSpeed_MeterPerSecond +
                                            objective.time_Second

                                    calcCostOnEdge(salesman, fromCenterEdge) +
                                            calcCostOnNode(salesman, objective)
                                }
                                else -> {
                                    takenCapacity.volume += objective.volume_Stere
                                    takenCapacity.weight += objective.weight_Gramm
                                    takenCapacity.time += addedTimeWithReturn

                                    calcCostOnEdge(salesman, fromCenterEdge) +
                                            calcCostOnNode(salesman, objective) +
                                            calcCostOnEdge(salesman, toCenterEdge)
                                }
                            }
                        }
                        slice.size - 1 -> {
                            val betweenEdge = task.costGraph.getEdgeBetween(slice[index - 1], value)
                            when {
                                couldSalesmanTakePreviousObjective(
                                    takenCapacity,
                                    salesman
                                ) -> {
                                    calcCostOnEdge(salesman, betweenEdge) +
                                            calcCostOnNode(salesman, objective) +
                                            calcCostOnEdge(salesman, toCenterEdge)
                                }
                                else -> {
                                    calcCostOnEdge(salesman, fromCenterEdge) +
                                            calcCostOnNode(salesman, objective) +
                                            calcCostOnEdge(salesman, toCenterEdge)
                                }
                            }
                        }
                        else -> {
                            val betweenEdge = task.costGraph.getEdgeBetween(slice[index - 1], value)

                            val addedTimeWithReturn =
                                betweenEdge.length_Meter / salesman.vechicleSpeed_MeterPerSecond +
                                        objective.time_Second +
                                        toCenterEdge.length_Meter / salesman.vechicleSpeed_MeterPerSecond

                            when {
                                canSalesmanTakeObjective(
                                    takenCapacity,
                                    objective,
                                    addedTimeWithReturn,
                                    salesman
                                ) -> {
                                    takenCapacity.volume += objective.volume_Stere
                                    takenCapacity.weight += objective.weight_Gramm
                                    takenCapacity.time += betweenEdge.length_Meter / salesman.vechicleSpeed_MeterPerSecond +
                                            objective.time_Second

                                    calcCostOnEdge(salesman, betweenEdge) +
                                            calcCostOnNode(salesman, objective)
                                }
                                couldSalesmanTakePreviousObjective(
                                    takenCapacity,
                                    salesman
                                ) -> {
                                    takenCapacity.volume += objective.volume_Stere
                                    takenCapacity.weight += objective.weight_Gramm
                                    takenCapacity.time += betweenEdge.length_Meter / salesman.vechicleSpeed_MeterPerSecond +
                                            objective.time_Second +
                                            toCenterEdge.length_Meter / salesman.vechicleSpeed_MeterPerSecond

                                    calcCostOnEdge(salesman, betweenEdge) +
                                            calcCostOnNode(salesman, objective) +
                                            calcCostOnEdge(salesman, toCenterEdge)
                                }
                                else -> {
                                    calcCostOnEdge(salesman, fromCenterEdge) +
                                            calcCostOnNode(salesman, objective) +
                                            calcCostOnEdge(salesman, toCenterEdge)
                                }
                            }
                        }
                    }

                }
                sumCost += cost

            }
            specimen.cost = sumCost
            specimen.costCalculated = true
            if (sumCost == 0.0) {
                println("Impossible!")
            }
        }
    }

    private fun canSalesmanTakeObjective(
        takenCapacity: TakenCapacity,
        objective: DObjective,
        additionalTime: Long,
        salesman: DSalesman
    ): Boolean {
        return takenCapacity.volume + objective.volume_Stere < salesman.volumeCapacity_Stere &&
                takenCapacity.weight + objective.weight_Gramm < salesman.weightCapacity_Gramm &&
                takenCapacity.time + additionalTime < salesman.workTime_SecondPerDay
    }

    private fun couldSalesmanTakePreviousObjective(
        takenCapacity: TakenCapacity,
        salesman: DSalesman
    ): Boolean {
        return takenCapacity.volume < salesman.volumeCapacity_Stere &&
                takenCapacity.weight < salesman.weightCapacity_Gramm &&
                takenCapacity.time < salesman.workTime_SecondPerDay
    }

    private fun calcCostOnEdge(salesman: DSalesman, edge: DEdge) =
        salesman.fuelPrice_EuroPerLiter * salesman.fuelConsuption_LiterPerMeter * edge.length_Meter +
                salesman.payment_EuroPerSecond * edge.length_Meter / salesman.vechicleSpeed_MeterPerSecond

    private fun calcCostOnNode(salesman: DSalesman, objective: DObjective) =
        salesman.payment_EuroPerSecond * objective.time_Second
}