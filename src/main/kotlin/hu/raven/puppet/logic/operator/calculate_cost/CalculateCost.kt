package hu.raven.puppet.logic.operator.calculate_cost

import hu.akos.hollo.szabo.math.vector.FloatVector
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.task.Task

sealed class CalculateCost {
    protected abstract val task: Task

    abstract operator fun invoke(solution: OnePartRepresentation): FloatVector
}