package hu.raven.puppet.logic.operator.initialize_population

import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId

sealed interface InitializePopulation {
    operator fun invoke(): List<OnePartRepresentationWithCostAndIterationAndId>
}