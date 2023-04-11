package hu.raven.puppet.logic.step.crossoverstrategy

import hu.raven.puppet.logic.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.physics.PhysicsUnit


abstract class CrossOverStrategy<C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<C> {
    abstract val crossoverOperators: List<CrossOverOperator<C>>
}