package hu.raven.puppet.logic.step.common.calculatecost

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.physics.Stere
import hu.raven.puppet.model.physics.sum
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.inject

class CalculateCostOfCVRPSolutionWithCapacityAndMaxTripLength<S : ISpecimenRepresentation<Meter>> :
    CalculateCost<S, Meter>() {
    val doubleLogger: DoubleLogger by inject()
    override fun invoke(specimen: ISpecimenRepresentation<Meter>) {
        statistics.fitnessCallCount++
        taskHolder.run {
            var sumCost: Meter? = Meter(0)
            var startCount = 0
            var endCount = 0
            specimen.forEachSliceIndexed { sliceIndex, slice ->
                //val salesman = task.salesmen[sliceIndex]
                var takenCapacity = Stere(0)
                sumCost = max(
                    sumCost,
                    slice.mapIndexed { index, value ->
                        when (index) {
                            0 -> {
                                startCount++
                                val fromCenterEdge = task.costGraph.edgesFromCenter[value]
                                takenCapacity += task.costGraph.objectives[value].volume
                                if (index != slice.size - 1) {
                                    fromCenterEdge.length
                                } else {
                                    endCount++
                                    val toCenterEdge = task.costGraph.edgesToCenter[value]
                                    fromCenterEdge.length + toCenterEdge.length
                                }
                            }

                            slice.size - 1 -> {
                                endCount++
                                //if (takenCapacity + task.costGraph.objectives[value].volume_Stere < salesman.volumeCapacity_Stere) {
                                val betweenEdge = task.costGraph.getEdgeBetween(slice[index - 1], value)
                                val toCenterEdge = task.costGraph.edgesToCenter[value]
                                takenCapacity += task.costGraph.objectives[value].volume
                                (betweenEdge.length + toCenterEdge.length)
                                /*} else {
                                    val fromPreviousToCenterEdge = task.costGraph.edgesFromCenter[slice[index - 1]]
                                    val fromCenterEdge = task.costGraph.edgesFromCenter[value]
                                    val toCenterEdge = task.costGraph.edgesFromCenter[value]
                                    takenCapacity += task.costGraph.objectives[value].volume_Stere
                                    (
                                            fromPreviousToCenterEdge.length_Meter +
                                                    fromCenterEdge.length_Meter +
                                                    toCenterEdge.length_Meter
                                            ).toDouble() / 1000.0
                                }

                                 */
                            }

                            else -> {
                                //if (takenCapacity + task.costGraph.objectives[value].volume_Stere < salesman.volumeCapacity_Stere) {
                                val betweenEdge = task.costGraph.getEdgeBetween(slice[index - 1], value)
                                betweenEdge.length
                                /* } else{
                                     val fromPreviousToCenterEdge = task.costGraph.edgesFromCenter[slice[index - 1]]
                                     val fromCenterEdge = task.costGraph.edgesFromCenter[value]
                                     (fromPreviousToCenterEdge.length_Meter + fromCenterEdge.length_Meter).toDouble() / 1000.0
                                 }

                                 */
                            }
                        }

                    }.toTypedArray().sum()
                )
            }
            if (endCount != startCount) {
                doubleLogger("startCount: $startCount endCount: $endCount")
            }
            specimen.cost = sumCost!!
            if (sumCost == Meter(0)) {
                println("Impossible!")
            }
        }
    }

    fun max(left: Meter?, right: Meter?) =
        when {
            left == null || right == null -> null
            left.value > right.value -> left
            left.value < right.value -> right
            else -> null
        }
}