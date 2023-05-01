package hu.raven.puppet.logic.step.calculatecost


import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.task.Task

sealed class CalculateCost {
    abstract val task: Task

    abstract operator fun invoke(solution: OnePartRepresentation): Fraction
}