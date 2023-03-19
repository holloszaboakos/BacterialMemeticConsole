package hu.raven.puppet.logic.step.calculatecost

import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.sumClever

class CalculateCostOfTspSolution<S : SolutionRepresentation<Meter>> : CalculateCost<S, Meter>() {
    override operator fun invoke(specimen: SolutionRepresentation<Meter>) {
        taskHolder.task.run {
            arrayOf(
                costGraph.edgesFromCenter[specimen[0]].length,
                costGraph.edgesToCenter[specimen[specimen.permutationSize - 1]].length,
                *specimen
                    .map { it }
                    .mapIndexed { index, value ->
                        if (index == 0) {
                            return@mapIndexed Meter(Fraction.new(0))
                        }

                        costGraph.getEdgeBetween(value, specimen[index - 1]).length
                    }.toList().toTypedArray()
            )
                .map { it.value }
                .sumClever()
        }
    }
}