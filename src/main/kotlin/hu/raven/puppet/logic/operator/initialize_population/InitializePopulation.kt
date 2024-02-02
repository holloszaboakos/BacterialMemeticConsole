package hu.raven.puppet.logic.operator.initialize_population

import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.task.Task

sealed interface InitializePopulation {
    operator fun invoke(task: Task): List<OnePartRepresentationWithCostAndIterationAndId>
}