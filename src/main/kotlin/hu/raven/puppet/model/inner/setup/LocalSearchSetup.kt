package hu.raven.puppet.model.inner.setup

import hu.raven.puppet.logic.common.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.common.steps.calculatecostofedge.CalculateCostOfEdge
import hu.raven.puppet.logic.common.steps.calculatecostofobjective.CalculateCostOfObjective
import hu.raven.puppet.logic.localsearch.initialize.InitializeLocalSearch
import hu.raven.puppet.logic.localsearch.iteration.LocalSearchIteration

data class LocalSearchSetup(
    override val initialize: InitializeAlgorithm,
    override val cost: CalculateCost,
    override val costOfEdge: CalculateCostOfEdge,
    override val costOfObjective: CalculateCostOfObjective,
    val initializeLocalSearch: InitializeLocalSearch,
    val iteration: LocalSearchIteration
) : AAlgorithm4VRPSetup(initialize, cost, costOfEdge, costOfObjective)