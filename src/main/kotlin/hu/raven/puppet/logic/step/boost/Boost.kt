package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.physics.PhysicsUnit

sealed class Boost<C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<C> {
    abstract val boostOperator: BoostOperator<C>
}