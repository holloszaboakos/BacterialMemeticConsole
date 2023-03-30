package hu.raven.puppet.model.state

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.task.Task

class IterativeAlgorithmStateWithMultipleCandidates<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val task: Task
) : IterativeAlgorithmState {
    override var iteration = 0
    var population: MutableList<S> = mutableListOf()
    var copyOfBest: S? = null
    var copyOfWorst: S? = null
}