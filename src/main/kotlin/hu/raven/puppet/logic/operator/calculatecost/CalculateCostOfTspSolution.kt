package hu.raven.puppet.logic.operator.calculatecost

import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.sumClever

class CalculateCostOfTspSolution(
    override val task: Task
) : CalculateCost() {
    override operator fun invoke(solution: OnePartRepresentation): Fraction {
        return arrayOf(
            task.costGraph.edgesFromCenter[solution.permutation[0]].length.value,
            task.costGraph.edgesToCenter[solution.permutation[solution.permutation.indices.last]].length.value,
            *solution.permutation
                .map { it }
                .mapIndexed { index, value ->
                    if (index == 0) {
                        return@mapIndexed Fraction.new(0)
                    }

                    task.costGraph.getEdgeBetween(solution.permutation[index - 1], value).length.value
                }
                .toList()
                .toTypedArray()
        ).sumClever()
    }
}