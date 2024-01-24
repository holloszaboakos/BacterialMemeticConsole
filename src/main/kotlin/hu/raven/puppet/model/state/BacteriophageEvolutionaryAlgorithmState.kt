package hu.raven.puppet.model.state

import hu.raven.puppet.model.solution.BacteriophageSpecimen
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.solution.PoolWithSmartActivation
import hu.raven.puppet.model.task.Task

data class BacteriophageEvolutionaryAlgorithmState(
    override val task: Task,
    override val population: PoolWithSmartActivation<OnePartRepresentationWithCostAndIterationAndId>,
    val virusPopulation: PoolWithSmartActivation<BacteriophageSpecimen>,
) : EvolutionaryAlgorithmState(task, population)