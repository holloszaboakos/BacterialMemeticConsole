package hu.raven.puppet.logic.step.calculatecost

import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.sumClever

class CalculateCostOfTspSolution(
    override val task: Task
) : CalculateCost<Meter>() {
    override operator fun invoke(solution: OnePartRepresentation): Meter {
        return arrayOf(
            task.costGraph.edgesFromCenter[solution.permutation[0]].length,
            task.costGraph.edgesToCenter[solution.permutation[solution.permutation.indices.last]].length,
            *solution.permutation
                .map { it }
                .mapIndexed { index, value ->
                    if (index == 0) {
                        return@mapIndexed Meter(Fraction.new(0))
                    }

                    task.costGraph.getEdgeBetween(solution.permutation[index - 1], value).length
                }
                .toList()
                .toTypedArray()
        )
            .map(Meter::value)
            .sumClever()
            .let(::Meter)
    }
}