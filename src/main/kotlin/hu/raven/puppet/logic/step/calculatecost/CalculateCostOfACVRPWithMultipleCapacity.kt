package hu.raven.puppet.logic.step.calculatecost

import hu.raven.puppet.model.TakenCapacity
import hu.raven.puppet.model.physics.Euro
import hu.raven.puppet.model.physics.Second
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.task.DSalesman
import hu.raven.puppet.model.task.graph.DEdge
import hu.raven.puppet.model.task.graph.DGraph
import hu.raven.puppet.model.task.graph.DObjective
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.sumClever

class CalculateCostOfACVRPWithMultipleCapacity<S : SolutionRepresentation<Euro>> : CalculateCost<S, Euro>() {
    override operator fun invoke(
        specimen: SolutionRepresentation<Euro>
    ) {
        statistics.fitnessCallCount++
        specimen.cost = taskHolder.run {
            specimen.mapSliceIndexed { sliceIndex, slice ->
                val salesman = task.salesmen[sliceIndex]
                var takenCapacity = TakenCapacity()

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
                .let { Euro(it) }
        }
    }

    private fun onFirstValueOfSlice(
        costGraph: DGraph,
        sliceValue: Int,
        salesman: DSalesman,
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
        costGraph: DGraph,
        sliceValue: Int,
        salesman: DSalesman,
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
        costGraph: DGraph,
        sliceValue: Int,
        salesman: DSalesman,
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