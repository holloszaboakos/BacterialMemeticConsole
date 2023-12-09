package hu.raven.puppet.logic.operator.calculatecost

import hu.akos.hollo.szabo.math.FloatSumExtensions.sumClever
import hu.akos.hollo.szabo.math.vector.FloatVector
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.utility.extention.getEdgeBetween

class CalculateCostOfTspSolution(
    override val task: Task
) : CalculateCost() {
    override operator fun invoke(solution: OnePartRepresentation): FloatVector {
        if (!solution.permutation.checkFormat()) {
            throw Exception("Wrong solution format!")
        }

        return arrayOf(
            task.costGraph.edgesFromCenter[solution.permutation[0]].length.value,
            task.costGraph.edgesToCenter[solution.permutation[solution.permutation.indices.last]].length.value,
            *solution.permutation
                .map { it }
                .mapIndexed { index, value ->
                    if (index == 0) {
                        return@mapIndexed 0f
                    }

                    task.costGraph.getEdgeBetween(solution.permutation[index - 1], value).length.value
                }
                .toList()
                .toTypedArray()
        ).sumClever()
            .let { FloatVector.floatVectorOf(it) }
    }
}