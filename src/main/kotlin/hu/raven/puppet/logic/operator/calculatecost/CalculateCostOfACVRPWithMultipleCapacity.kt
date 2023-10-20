package hu.raven.puppet.logic.operator.calculatecost


import hu.raven.puppet.model.operator.calculatecost.TakenCapacity
import hu.raven.puppet.model.physics.Euro
import hu.raven.puppet.model.physics.Gram
import hu.raven.puppet.model.physics.Second
import hu.raven.puppet.model.physics.CubicMeter
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.task.*
import hu.raven.puppet.utility.extention.FloatSumExtensions.sumClever
import hu.raven.puppet.utility.extention.getEdgeBetween

class CalculateCostOfACVRPWithMultipleCapacity(
    override val task: Task
) : CalculateCost() {
    override operator fun invoke(
        solution: OnePartRepresentation
    ): Float {
        return solution.permutation
            .sliced { it >= solution.objectiveCount }
            .mapIndexed { sliceIndex, slice ->
                val salesman = task.transportUnits[sliceIndex]
                var takenCapacity = TakenCapacity(
                    volume = CubicMeter(0),
                    weight = Gram(0),
                    time = Second(0)
                )

                slice.mapIndexed { sliceValueIndex, sliceValue ->
                    when (sliceValueIndex) {
                        0 -> onFirstValueOfSlice(
                            task.costGraph,
                            sliceValue,
                            salesman,
                            takenCapacity,
                            sliceValue == slice.last(),
                        ).let {
                            takenCapacity = it.second
                            it.first
                        }

                        slice.lastIndex -> onLastValueOfSlice(
                            task.costGraph,
                            sliceValue,
                            salesman,
                            takenCapacity,
                            slice[sliceValueIndex - 1],
                        )

                        else -> onOtherValuesOfSlice(
                            task.costGraph,
                            sliceValue,
                            salesman,
                            takenCapacity,
                            slice[sliceValueIndex - 1],
                        ).let {
                            takenCapacity = it.second
                            it.first
                        }
                    }
                }
            }.flatten()
            .map { it.value }
            .sumClever()
    }

    private fun onFirstValueOfSlice(
        costGraph: CostGraph,
        sliceValue: Int,
        salesman: TransportUnit,
        takenCapacity: TakenCapacity,
        isLast: Boolean,
    ): Pair<Euro, TakenCapacity> {
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
                takenCapacity,
                objective,
                addedTimeWithReturn,
                salesman
            )
        ) {
            return Pair(
                second = TakenCapacity(
                    volume = takenCapacity.volume + objective.volume,
                    weight = takenCapacity.weight + objective.weight,
                    time = takenCapacity.time + fromCenterEdge.length / salesman.vehicleSpeed +
                            objective.time
                ),
                first = calcCostOnEdge(salesman, fromCenterEdge) +
                        calcCostOnNode(salesman, objective)
            )
        }

        return Pair(
            second = TakenCapacity(
                volume = takenCapacity.volume + objective.volume,
                weight = takenCapacity.weight + objective.weight,
                time = takenCapacity.time + addedTimeWithReturn
            ),
            first = calcCostOnEdge(salesman, fromCenterEdge) +
                    calcCostOnNode(salesman, objective) +
                    calcCostOnEdge(salesman, toCenterEdge)
        )
    }

    private fun onLastValueOfSlice(
        costGraph: CostGraph,
        sliceValue: Int,
        salesman: TransportUnit,
        takenCapacity: TakenCapacity,
        previousSliceValue: Int,
    ): Euro {
        val toCenterEdge = costGraph.edgesToCenter[sliceValue]
        val objective = costGraph.objectives[sliceValue]
        val fromCenterEdge = costGraph.edgesFromCenter[sliceValue]
        val betweenEdge = costGraph.getEdgeBetween(previousSliceValue, sliceValue)

        if (
            couldSalesmanTakePreviousObjective(
                takenCapacity,
                salesman
            )
        ) {
            return calcCostOnEdge(salesman, betweenEdge) +
                    calcCostOnNode(salesman, objective) +
                    calcCostOnEdge(salesman, toCenterEdge)
        }

        return calcCostOnEdge(salesman, fromCenterEdge) +
                calcCostOnNode(salesman, objective) +
                calcCostOnEdge(salesman, toCenterEdge)
    }

    private fun onOtherValuesOfSlice(
        costGraph: CostGraph,
        sliceValue: Int,
        salesman: TransportUnit,
        takenCapacity: TakenCapacity,
        previousSliceValue: Int,
    ): Pair<Euro, TakenCapacity> {
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
                takenCapacity,
                objective,
                addedTimeWithReturn,
                salesman
            ) -> {
                return Pair(
                    second = TakenCapacity(
                        volume = takenCapacity.volume + objective.volume,
                        weight = takenCapacity.weight + objective.weight,
                        time = takenCapacity.time +
                                betweenEdge.length / salesman.vehicleSpeed +
                                objective.time,
                    ),
                    first = calcCostOnEdge(salesman, betweenEdge) +
                            calcCostOnNode(salesman, objective)
                )
            }

            couldSalesmanTakePreviousObjective(
                takenCapacity,
                salesman
            ) -> {
                return Pair(
                    second = TakenCapacity(
                        volume = takenCapacity.volume + objective.volume,
                        weight = takenCapacity.weight + objective.weight,
                        time = takenCapacity.time +
                                betweenEdge.length / salesman.vehicleSpeed +
                                objective.time +
                                toCenterEdge.length / salesman.vehicleSpeed,
                    ),
                    first = calcCostOnEdge(salesman, betweenEdge) +
                            calcCostOnNode(salesman, objective) +
                            calcCostOnEdge(salesman, toCenterEdge)
                )
            }
        }
        return Pair(
            second = TakenCapacity(
                volume = takenCapacity.volume + objective.volume,
                weight = takenCapacity.weight + objective.weight,
                time = takenCapacity.time +
                        betweenEdge.length / salesman.vehicleSpeed +
                        objective.time +
                        toCenterEdge.length / salesman.vehicleSpeed,
            ),
            first = calcCostOnEdge(salesman, fromCenterEdge) +
                    calcCostOnNode(salesman, objective) +
                    calcCostOnEdge(salesman, toCenterEdge)
        )
    }

    private fun canSalesmanTakeObjective(
        takenCapacity: TakenCapacity,
        objective: CostGraphVertex,
        additionalTime: Second,
        salesman: TransportUnit
    ): Boolean {
        return takenCapacity.volume + objective.volume < salesman.volumeCapacity &&
                takenCapacity.weight + objective.weight < salesman.weightCapacity &&
                takenCapacity.time + additionalTime < salesman.workTimePerDay
    }

    private fun couldSalesmanTakePreviousObjective(
        takenCapacity: TakenCapacity,
        salesman: TransportUnit
    ): Boolean {
        return takenCapacity.volume < salesman.volumeCapacity &&
                takenCapacity.weight < salesman.weightCapacity &&
                takenCapacity.time < salesman.workTimePerDay
    }

    private fun calcCostOnEdge(salesman: TransportUnit, edge: CostGraphEdge) =
        salesman.fuelPrice * salesman.fuelConsumption * edge.length +
                salesman.salary * (edge.length / salesman.vehicleSpeed)

    private fun calcCostOnNode(salesman: TransportUnit, objective: CostGraphVertex) =
        salesman.salary * objective.time
}