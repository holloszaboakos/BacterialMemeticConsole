package hu.raven.puppet.model.state

import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.solution.PoolWithSmartActivation
import hu.raven.puppet.model.solution.VirusSpecimen
import hu.raven.puppet.model.task.Task

class VirusEvolutionaryAlgorithmState(
    task: Task,
    population: PoolWithSmartActivation<OnePartRepresentationWithCostAndIterationAndId>,
    val virusPopulation: PoolWithSmartActivation<VirusSpecimen>,
) : EvolutionaryAlgorithmState(task, population)