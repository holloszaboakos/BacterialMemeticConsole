package hu.raven.puppet.logic.operator.calculatecost

import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.physics.Stere
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.task.CostGraph
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.model.task.TransportUnit
import hu.raven.puppet.utility.extention.getEdgeBetween

class CalculateCostOfCVRPSolutionWithCapacityAndMaxTripLength(
    override val task: Task
) : CalculateCost() {
    private data class TripState(
        val takenCapacity: Stere,
        val cost: Meter,
    )

    override fun invoke(solution: OnePartRepresentation): Fraction {
        return solution.permutation
            .sliced { it >= task.costGraph.objectives.size - 1 }
            .mapIndexed { sliceIndex, slice ->
                val salesman = task.transportUnits[sliceIndex]
                var tripState = TripState(Stere(0L), Meter(0L))
                slice.forEachIndexed { sliceValueIndex, sliceValue ->
                    tripState = when (sliceValueIndex) {
                        0 -> onFirstValueOfSlice(
                            task.costGraph,
                            sliceValue,
                            tripState,
                            0 != slice.lastIndex
                        )

                        slice.size - 1 -> onLastValueOfSlice(
                            task.costGraph,
                            sliceValue,
                            tripState,
                            salesman,
                            slice[sliceValueIndex - 1],
                        )

                        else -> onOtherValuesOfSlice(
                            task.costGraph,
                            sliceValue,
                            tripState,
                            salesman,
                            slice[sliceValueIndex - 1],
                        )
                    }

                }
                tripState
            }
            .maxOf { it.cost.value }
    }

    private fun onFirstValueOfSlice(
        costGraph: CostGraph,
        sliceValue: Int,
        tripState: TripState,
        isLastValueOfSlice: Boolean
    ): TripState {
        val fromCenterEdge = costGraph.edgesFromCenter[sliceValue]
        val newTakenCapacity = tripState.takenCapacity + costGraph.objectives[sliceValue].volume
        if (isLastValueOfSlice) {
            return TripState(
                takenCapacity = newTakenCapacity,
                cost = tripState.cost + fromCenterEdge.length,
            )
        }

        val toCenterEdge = costGraph.edgesToCenter[sliceValue]

        return TripState(
            takenCapacity = newTakenCapacity,
            cost = tripState.cost +
                    fromCenterEdge.length +
                    toCenterEdge.length,
        )
    }

    private fun onLastValueOfSlice(
        costGraph: CostGraph,
        sliceValue: Int,
        tripState: TripState,
        salesman: TransportUnit,
        previousSliceValue: Int,
    ): TripState {
        if (tripState.takenCapacity + costGraph.objectives[sliceValue].volume < salesman.volumeCapacity) {
            val betweenEdge = costGraph.getEdgeBetween(previousSliceValue, sliceValue)
            val toCenterEdge = costGraph.edgesToCenter[sliceValue]
            return TripState(
                tripState.takenCapacity + costGraph.objectives[sliceValue].volume,
                betweenEdge.length + toCenterEdge.length
            )


        }

        val fromPreviousToCenterEdge =
            costGraph.edgesFromCenter[previousSliceValue]
        val fromCenterEdge = costGraph.edgesFromCenter[sliceValue]
        val toCenterEdge = costGraph.edgesFromCenter[sliceValue]

        return TripState(
            tripState.takenCapacity + costGraph.objectives[sliceValue].volume,
            tripState.cost +
                    fromPreviousToCenterEdge.length +
                    fromCenterEdge.length +
                    toCenterEdge.length
        )
    }

    private fun onOtherValuesOfSlice(
        costGraph: CostGraph,
        sliceValue: Int,
        tripState: TripState,
        salesman: TransportUnit,
        previousSliceValue: Int
    ): TripState {
        if (tripState.takenCapacity + costGraph.objectives[sliceValue].volume < salesman.volumeCapacity) {
            val betweenEdge = costGraph.getEdgeBetween(previousSliceValue, sliceValue)

            return tripState.copy(
                cost = tripState.cost + betweenEdge.length
            )
        }

        val fromPreviousToCenterEdge =
            costGraph.edgesFromCenter[previousSliceValue]
        val fromCenterEdge = costGraph.edgesFromCenter[sliceValue]

        return tripState.copy(
            cost = tripState.cost +
                    fromPreviousToCenterEdge.length +
                    fromCenterEdge.length
        )
    }
}