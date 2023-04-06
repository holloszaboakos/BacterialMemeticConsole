package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation

sealed class BoostOperator<C : PhysicsUnit<C>> {
    abstract val calculateCostOf: CalculateCost<C>

    abstract operator fun invoke(specimen: OnePartRepresentation<C>): StepEfficiencyData
}