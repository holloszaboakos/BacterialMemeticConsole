package hu.raven.puppet.logic.evolutionary.setup

import hu.raven.puppet.logic.common.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.common.steps.calculatecostofedge.CalculateCostOfEdge
import hu.raven.puppet.logic.common.steps.calculatecostofobjective.CalculateCostOfObjective
import hu.raven.puppet.logic.evolutionary.common.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.evolutionary.common.OrderPopulationByCost
import hu.raven.puppet.logic.evolutionary.common.iteration.EvolutionaryIteration
import hu.raven.puppet.logic.evolutionary.common.boost.Boost
import hu.raven.puppet.model.inner.setup.AAlgorithm4VRPSetup

sealed class SEvolutionaryAlgorithmSetup (
    override val initialize: InitializeAlgorithm,

    open val iteration: EvolutionaryIteration,

    override val cost: CalculateCost,
    override val costOfEdge: CalculateCostOfEdge,
    override val costOfObjective: CalculateCostOfObjective,

    open val initializePopulation: InitializePopulation,
    open val orderByCost: OrderPopulationByCost,
    open val boost: Boost
): AAlgorithm4VRPSetup(initialize, cost, costOfEdge, costOfObjective)