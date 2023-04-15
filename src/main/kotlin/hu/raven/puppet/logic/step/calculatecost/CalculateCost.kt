package hu.raven.puppet.logic.step.calculatecost


import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.task.Task

sealed class CalculateCost<C : PhysicsUnit<C>> {
    abstract val task: Task

    abstract operator fun invoke(solution: OnePartRepresentation): C
}