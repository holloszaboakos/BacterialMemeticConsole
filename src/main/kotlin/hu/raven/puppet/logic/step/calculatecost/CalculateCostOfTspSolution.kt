package hu.raven.puppet.logic.step.calculatecost

import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.AlgorithmState
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.sumClever

class CalculateCostOfTspSolution(
    override val statistics: BacterialAlgorithmStatistics,
    val algorithmState: AlgorithmState
) : CalculateCost<Meter>() {
    override operator fun invoke(specimen: OnePartRepresentation<Meter>) {
        algorithmState.task.run {
            specimen.cost = arrayOf(
                costGraph.edgesFromCenter[specimen.permutation[0]].length,
                costGraph.edgesToCenter[specimen.permutation[specimen.permutation.indices.last]].length,
                *specimen.permutation
                    .map { it }
                    .mapIndexed { index, value ->
                        if (index == 0) {
                            return@mapIndexed Meter(Fraction.new(0))
                        }

                        costGraph.getEdgeBetween(specimen.permutation[index - 1], value).length
                    }
                    .toList()
                    .toTypedArray()
            )
                .map(Meter::value)
                .sumClever()
                .let(::Meter)
        }
    }
}