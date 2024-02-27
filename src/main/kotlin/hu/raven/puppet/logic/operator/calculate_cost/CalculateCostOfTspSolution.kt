@file:Suppress("ReplaceGetOrSet")

package hu.raven.puppet.logic.operator.calculate_cost

import hu.akos.hollo.szabo.math.vector.FloatVector
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.floatVectorOf
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.utility.math.CompleteGraph

private typealias Task = CompleteGraph<Unit, Int>

class CalculateCostOfTspSolution(
    override val task: Task
) : CalculateCost<Task>() {
    override operator fun invoke(solution: OnePartRepresentation): FloatVector {
        var result = 0

        result += task.edges.last()[solution.permutation[0]].value
        result += (1 until solution.permutation.size)
            .sumOf { index ->
                task.edges
                    .get(solution.permutation[index - 1])
                    .get(solution.permutation[index])
                    .value
            }
        result += task.edges[solution.permutation.last()].last().value

        return floatVectorOf(result.toFloat())
    }
}