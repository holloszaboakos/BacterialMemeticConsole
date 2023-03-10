package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.inject

sealed class BoostOperator<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<S, C>() {
    val calculateCostOf: CalculateCost<S, C> by inject()

    abstract operator fun invoke(specimen: S): StepEfficiencyData
}