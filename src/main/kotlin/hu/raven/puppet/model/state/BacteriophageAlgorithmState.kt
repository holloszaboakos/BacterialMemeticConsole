package hu.raven.puppet.model.state

import hu.raven.puppet.model.solution.BacteriophageSpecimen
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.solution.PoolWithSmartActivation
import hu.raven.puppet.model.task.Task

class BacteriophageAlgorithmState(
    task: Task,
    population: PoolWithSmartActivation<OnePartRepresentationWithCostAndIterationAndId>,
    val virusPopulation: PoolWithSmartActivation<BacteriophageSpecimen>,
) : EvolutionaryAlgorithmState(task, population)