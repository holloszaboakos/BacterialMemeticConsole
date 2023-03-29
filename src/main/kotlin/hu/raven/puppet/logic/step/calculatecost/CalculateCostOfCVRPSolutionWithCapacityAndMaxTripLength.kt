package hu.raven.puppet.logic.step.calculatecost

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.physics.Stere
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.sumClever

class CalculateCostOfCVRPSolutionWithCapacityAndMaxTripLength<S : SolutionRepresentation<Meter>>(
    override val logger: DoubleLogger,
    override val taskHolder: VRPTaskHolder,
    override val subSolutionFactory: SolutionRepresentationFactory<S, Meter>,
    override val statistics: BacterialAlgorithmStatistics,
    val doubleLogger: DoubleLogger
) :
    CalculateCost<S, Meter>() {
    override fun invoke(specimen: SolutionRepresentation<Meter>) {
        statistics.fitnessCallCount++
        taskHolder.run {
            var sumCost: Fraction? = Fraction.new(0L)
            var startCount = 0
            var endCount = 0
            specimen.forEachSliceIndexed { _, slice ->
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

                    }
                        .map { it.value }
                        .toTypedArray()
                        .sumClever()
                )
            }
            if (endCount != startCount) {
                doubleLogger("startCount: $startCount endCount: $endCount")
            }
            specimen.cost = Meter(sumCost!!)
            if (sumCost == Fraction.new(0L)) {
                println("Impossible!")
            }
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