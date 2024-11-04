package hu.raven.puppet.logic.operator.initialize_population

import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration

sealed interface InitializePopulation {
    operator fun invoke(): List<OnePartRepresentationWithCostAndIteration>
}