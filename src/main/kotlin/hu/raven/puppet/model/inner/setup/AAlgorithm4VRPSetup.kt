package hu.raven.puppet.model.inner.setup

import hu.raven.puppet.logic.common.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.common.steps.calculatecostofedge.CalculateCostOfEdge
import hu.raven.puppet.logic.common.steps.calculatecostofobjective.CalculateCostOfObjective

abstract class AAlgorithm4VRPSetup (
    open val initialize: InitializeAlgorithm,
    open val cost: CalculateCost,
    open val costOfEdge: CalculateCostOfEdge,
    open val costOfObjective: CalculateCostOfObjective,
    )