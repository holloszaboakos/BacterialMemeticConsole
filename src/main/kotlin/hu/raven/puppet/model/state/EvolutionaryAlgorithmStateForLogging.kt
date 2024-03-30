package hu.raven.puppet.model.state

import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId

open class EvolutionaryAlgorithmStateForLogging(
    val population: List<OnePartRepresentationWithCostAndIterationAndId>,
    val iteration: Int,
)