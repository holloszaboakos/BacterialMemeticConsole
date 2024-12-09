@file:Suppress("ReplaceGetOrSet")

package hu.raven.puppet.logic.operator.calculate_cost

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.vector.FloatVector
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.floatVectorOf
import hu.raven.puppet.model.task.TspTask

class CalculateCostOfTspSolution(
    override val task: TspTask
) : CalculateCost<Permutation, TspTask>() {
    override fun invoke(representation: Permutation): FloatVector {
        var result = 0

        result += task.distanceMatrix.edges.asList().last()[representation[0]]
        result += (1 until representation.size)
            .sumOf { index ->
                task.distanceMatrix.edges
                    .get(representation[index - 1])
                    .get(representation[index])
            }
        result += task.distanceMatrix.edges[representation.last()].asList().last()

        return floatVectorOf(result.toFloat())
    }
}