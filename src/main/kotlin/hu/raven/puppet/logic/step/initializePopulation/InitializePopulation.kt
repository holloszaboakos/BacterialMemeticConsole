package hu.raven.puppet.logic.step.initializePopulation

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.task.Task

sealed class InitializePopulation<C : PhysicsUnit<C>> {
    abstract operator fun invoke(task: Task): List<OnePartRepresentationWithCostAndIterationAndId<C>>
}