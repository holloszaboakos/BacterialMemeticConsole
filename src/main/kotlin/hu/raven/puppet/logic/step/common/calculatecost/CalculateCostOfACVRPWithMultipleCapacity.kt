package hu.raven.puppet.logic.step.common.calculatecost

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.TakenCapacity
import hu.raven.puppet.model.TripState
import hu.raven.puppet.model.physics.Euro
import hu.raven.puppet.model.physics.Second
import hu.raven.puppet.model.task.DSalesman
import hu.raven.puppet.model.task.graph.DEdge
import hu.raven.puppet.model.task.graph.DGraph
import hu.raven.puppet.model.task.graph.DObjective
import hu.raven.puppet.utility.extention.getEdgeBetween

class CalculateCostOfACVRPWithMultipleCapacity<S : ISpecimenRepresentation<Euro>> : CalculateCost<S, Euro>() {
    override operator fun invoke(
        specimen: ISpecimenRepresentation<Euro>
    ) {
        statistics.fitnessCallCount++
        taskHolder.run {
            var sumCost = Euro(0)
            specimen.forEachSliceIndexed { sliceIndex, slice ->
                val salesman = task.salesmen[sliceIndex]
                var tripState = TripState(
                    TakenCapacity(),
                    salesman.basePrice
                )

                slice.forEachIndexed { sliceValueIndex, sliceValue ->
                    tripState = when (sliceValueIndex) {
                        0 -> onFirstValueOfSlice(
                            task.costGraph,
                            sliceValue,
                            salesman,
                            tripState,
                            sliceValue == slice.last(),
                        )

                        slice.lastIndex -> onLastValueOfSlice(
                            task.costGraph,
                            sliceValue,
                            salesman,
                            tripState,
                            slice[sliceValueIndex - 1],
                        )

                        else -> onOtherValuesOfSlice(
                            task.costGraph,
                            sliceValue,
                            salesman,
                            tripState,
                            slice[sliceValueIndex - 1],
                        )
                    }
                }
                sumCost += tripState.cost
            }
            specimen.cost = sumCost
            if (sumCost == Euro(0)) {
                println("Impossible!")
            }
        }
    }

    private fun onFirstValueOfSlice(
        costGraph: DGraph,
        sliceValue: Int,
        salesman: DSalesman,
        tripState: TripState,
        isLast: Boolean,
    ): TripState {
        val toCenterEdge = costGraph.edgesToCenter[sliceValue]
        val objective = costGraph.objectives[sliceValue]
        val fromCenterEdge = costGraph.edgesFromCenter[sliceValue]
        val addedTimeWithReturn =
            fromCenterEdge.length / salesman.vehicleSpeed +
                    objective.time +
                    toCenterEdge.length / salesman.vehicleSpeed

        if (
            !isLast &&
            canSalesmanTakeObjective(
                tripState.takenCapacity,
                objective,
                addedTimeWithReturn,
                salesman
            )
        ) {
            return TripState(
                TakenCapacity(
                    volume = tripState.takenCapacity.volume + objective.volume,
                    weight = tripState.takenCapacity.weight + objective.weight,
                    time = tripState.takenCapacity.time + fromCenterEdge.length / salesman.vehicleSpeed +
                            objective.time
                ),
                cost = tripState.cost +
                        calcCostOnEdge(salesman, fromCenterEdge) +
                        calcCostOnNode(salesman, objective)
            )
        }

        return TripState(
            TakenCapacity(
                volume = tripState.takenCapacity.volume + objective.volume,
                weight = tripState.takenCapacity.weight + objective.weight,
                time = tripState.takenCapacity.time + addedTimeWithReturn
            ),
            cost = tripState.cost +
                    calcCostOnEdge(salesman, fromCenterEdge) +
                    calcCostOnNode(salesman, objective) +
                    calcCostOnEdge(salesman, toCenterEdge)
        )
    }

    private fun onLastValueOfSlice(
        costGraph: DGraph,
        sliceValue: Int,
        salesman: DSalesman,
        tripState: TripState,
        previousSliceValue: Int,
    ): TripState {
        val toCenterEdge = costGraph.edgesToCenter[sliceValue]
        val objective = costGraph.objectives[sliceValue]
        val fromCenterEdge = costGraph.edgesFromCenter[sliceValue]
        val betweenEdge = costGraph.getEdgeBetween(previousSliceValue, sliceValue)

        if (
            couldSalesmanTakePreviousObjective(
                tripState.takenCapacity,
                salesman
            )
        ) {
            return tripState.copy(
                cost = tripState.cost +
                        calcCostOnEdge(salesman, betweenEdge) +
                        calcCostOnNode(salesman, objective) +
                        calcCostOnEdge(salesman, toCenterEdge)
            )
        }

        return tripState.copy(
            cost = tripState.cost + calcCostOnEdge(salesman, fromCenterEdge) +
                    calcCostOnNode(salesman, objective) +
                    calcCostOnEdge(salesman, toCenterEdge)
        )
    }

    private fun onOtherValuesOfSlice(
        costGraph: DGraph,
        sliceValue: Int,
        salesman: DSalesman,
        tripState: TripState,
        previousSliceValue: Int,
    ): TripState {
        val toCenterEdge = costGraph.edgesToCenter[sliceValue]
        val objective = costGraph.objectives[sliceValue]
        val fromCenterEdge = costGraph.edgesFromCenter[sliceValue]
        val betweenEdge = costGraph.getEdgeBetween(previousSliceValue, sliceValue)

        val addedTimeWithReturn =
            betweenEdge.length / salesman.vehicleSpeed +
                    objective.time +
                    toCenterEdge.length / salesman.vehicleSpeed

        when {
            canSalesmanTakeObjective(
                tripState.takenCapacity,
                objective,
                addedTimeWithReturn,
                salesman
            ) -> {
                return TripState(
                    TakenCapacity(
                        volume = tripState.takenCapacity.volume + objective.volume,
                        weight = tripState.takenCapacity.weight + objective.weight,
                        time = tripState.takenCapacity.time +
                                betweenEdge.length / salesman.vehicleSpeed +
                                objective.time,
                    ),
                    cost = tripState.cost +
                            calcCostOnEdge(salesman, betweenEdge) +
                            calcCostOnNode(salesman, objective)
                )
            }

            couldSalesmanTakePreviousObjective(
                tripState.takenCapacity,
                salesman
            ) -> {
                return TripState(
                    TakenCapacity(
                        volume = tripState.takenCapacity.volume + objective.volume,
                        weight = tripState.takenCapacity.weight + objective.weight,
                        time = tripState.takenCapacity.time +
                                betweenEdge.length / salesman.vehicleSpeed +
                                objective.time +
                                toCenterEdge.length / salesman.vehicleSpeed,
                    ),
                    cost = tripState.cost +
                            calcCostOnEdge(salesman, betweenEdge) +
                            calcCostOnNode(salesman, objective) +
                            calcCostOnEdge(salesman, toCenterEdge)
                )
            }
        }
        return TripState(
            TakenCapacity(
                volume = tripState.takenCapacity.volume + objective.volume,
                weight = tripState.takenCapacity.weight + objective.weight,
                time = tripState.takenCapacity.time +
                        betweenEdge.length / salesman.vehicleSpeed +
                        objective.time +
                        toCenterEdge.length / salesman.vehicleSpeed,
            ),
            cost = tripState.cost +
                    calcCostOnEdge(salesman, fromCenterEdge) +
                    calcCostOnNode(salesman, objective) +
                    calcCostOnEdge(salesman, toCenterEdge)
        )
    }

    private fun canSalesmanTakeObjective(
        takenCapacity: TakenCapacity,
        objective: DObjective,
        additionalTime: Second,
        salesman: DSalesman
    ): Boolean {
        return takenCapacity.volume + objective.volume < salesman.volumeCapacity &&
                takenCapacity.weight + objective.weight < salesman.weightCapacity &&
                takenCapacity.time + additionalTime < salesman.workTimePerDay
    }

    private fun couldSalesmanTakePreviousObjective(
        takenCapacity: TakenCapacity,
        salesman: DSalesman
    ): Boolean {
        return takenCapacity.volume < salesman.volumeCapacity &&
                takenCapacity.weight < salesman.weightCapacity &&
                takenCapacity.time < salesman.workTimePerDay
    }

    private fun calcCostOnEdge(salesman: DSalesman, edge: DEdge) =
        salesman.fuelPrice * salesman.fuelConsumption * edge.length +
                salesman.salary * (edge.length / salesman.vehicleSpeed)

    private fun calcCostOnNode(salesman: DSalesman, objective: DObjective) =
        salesman.salary * objective.time
}