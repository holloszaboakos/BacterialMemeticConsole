package hu.raven.puppet.logic.step.booststrategy

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId

sealed class BoostStrategy<C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<C> {
    abstract val boostOperator: BoostOperator<C, OnePartRepresentationWithCostAndIterationAndId<C>>
}