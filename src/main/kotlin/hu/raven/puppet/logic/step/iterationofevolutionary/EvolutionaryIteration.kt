package hu.raven.puppet.logic.step.iterationofevolutionary

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit

sealed class EvolutionaryIteration<C : PhysicsUnit<C>> :
    EvolutionaryAlgorithmStep<C>() {
    abstract val logger: DoubleLogger
    abstract operator fun invoke()
}