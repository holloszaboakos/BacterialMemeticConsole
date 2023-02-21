package hu.raven.puppet.logic.step.common.calculatecost

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.physics.Stere
import hu.raven.puppet.model.task.DSalesman
import hu.raven.puppet.model.task.graph.DGraph
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.inject

class CalculateCostOfCVRPSolutionWithCapacity<S : ISpecimenRepresentation<Meter>> : CalculateCost<S, Meter>() {
    data class TripState(
        val takenCapacity: Stere,
        val cost: Meter,
    )

    val doubleLogger: DoubleLogger by inject()
    override fun invoke(specimen: ISpecimenRepresentation<Meter>) {
        statistics.fitnessCallCount++
        taskHolder.run {
            var tripState = TripState(Stere(0), Meter(0))
            specimen.forEachSliceIndexed { sliceIndex, slice ->
                val salesman = task.salesmen[sliceIndex]
                slice.forEachIndexed { sliceValueIndex, sliceValue ->
                    tripState = when (sliceValueIndex) {
                        0 -> onFirstValueOfSlice(
                            task.costGraph,
                            sliceValue,
                            tripState,
                            sliceValueIndex != slice.lastIndex
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
            }
            specimen.cost = tripState.cost
            specimen.costCalculated = true
            if (tripState.cost == Meter(0)) {
                println("Impossible!")
            }
        }
    }

    private fun onFirstValueOfSlice(
        costGraph: DGraph,
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
        costGraph: DGraph,
        sliceValue: Int,
        tripState: TripState,
        salesman: DSalesman,
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
        costGraph: DGraph,
        sliceValue: Int,
        tripState: TripState,
        salesman: DSalesman,
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