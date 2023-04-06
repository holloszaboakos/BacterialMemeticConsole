package hu.raven.puppet.logic.step.diversity

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit

sealed class Diversity<C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<C>() {
    abstract operator fun invoke(): Double
}