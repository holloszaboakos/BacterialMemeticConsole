package hu.raven.puppet.logic.step.evolutionary.common.boostoperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.evolutionary.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.utility.inject

sealed class BoostOperator<S : ISpecimenRepresentation> : EvolutionaryAlgorithmStep<S>() {
    val calculateCostOf: CalculateCost<S> by inject()

    abstract operator fun invoke(specimen: S): StepEfficiencyData
}