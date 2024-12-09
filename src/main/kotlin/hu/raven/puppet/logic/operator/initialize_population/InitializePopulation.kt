package hu.raven.puppet.logic.operator.initialize_population

import hu.raven.puppet.model.solution.SolutionWithIteration

sealed interface InitializePopulation<R> {
    operator fun invoke(): List<SolutionWithIteration<R>>
}