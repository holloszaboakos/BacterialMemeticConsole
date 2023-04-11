package hu.raven.puppet.logic.step.calculatecost

import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.physics.Stere
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.sumClever

class CalculateCostOfCVRPSolutionWithCapacityAndMaxTripLength(
    override val task: Task
) : CalculateCost<Meter>() {
    override fun invoke(specimen: OnePartRepresentationWithIteration<Meter>) {
        var sumCost: Fraction? = Fraction.new(0L)
        var startCount = 0
        var endCount = 0
        specimen.permutation
            .sliced { it >= specimen.objectiveCount }
            .forEachIndexed { _, slice ->
                //val salesman = task.salesmen[sliceIndex]
                var takenCapacity = Stere(0L)
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
                                TODO
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
                                TODO
                                     val fromPreviousToCenterEdge = task.costGraph.edgesFromCenter[slice[index - 1]]
                                     val fromCenterEdge = task.costGraph.edgesFromCenter[value]
                                     (fromPreviousToCenterEdge.length_Meter + fromCenterEdge.length_Meter).toDouble() / 1000.0
                                 }

                                 */
                            }
                        }

                    }
                        .map { it.value }
                        .toTypedArray()
                        .sumClever()
                )
            }
        specimen.cost = Meter(sumCost!!)
        if (sumCost == Fraction.new(0L)) {
            println("Impossible!")
        }
    }


    private fun max(left: Fraction?, right: Fraction?) =
        when {
            left == null || right == null -> null
            left > right -> left
            left < right -> right
            else -> null
        }
}