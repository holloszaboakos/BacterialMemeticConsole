package hu.raven.puppet.logic.step.initializePopulation

import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.task.Task

sealed class InitializePopulation {
    abstract operator fun invoke(task: Task): List<OnePartRepresentationWithCostAndIterationAndId>
}