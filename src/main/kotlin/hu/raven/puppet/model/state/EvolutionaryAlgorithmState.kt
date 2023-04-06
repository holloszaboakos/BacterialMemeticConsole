package hu.raven.puppet.model.state

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.task.Task

data class EvolutionaryAlgorithmState<C : PhysicsUnit<C>>(
    override val task: Task,
    override var iteration: Int,
    val population: List<OnePartRepresentation<C>>,
    val copyOfBest: OnePartRepresentation<C>,
    val copyOfWorst: OnePartRepresentation<C>,
) : IterativeAlgorithmState