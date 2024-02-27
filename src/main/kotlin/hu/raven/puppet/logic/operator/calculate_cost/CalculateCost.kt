package hu.raven.puppet.logic.operator.calculate_cost

import hu.akos.hollo.szabo.math.vector.FloatVector
import hu.raven.puppet.model.solution.OnePartRepresentation

sealed class CalculateCost<T> {
    protected abstract val task: T

    abstract operator fun invoke(solution: OnePartRepresentation): FloatVector
}