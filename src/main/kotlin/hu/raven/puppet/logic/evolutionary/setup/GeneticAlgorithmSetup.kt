package hu.raven.puppet.logic.evolutionary.setup

import hu.raven.puppet.logic.common.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.common.steps.calculatecostofedge.CalculateCostOfEdge
import hu.raven.puppet.logic.common.steps.calculatecostofobjective.CalculateCostOfObjective
import hu.raven.puppet.logic.evolutionary.common.OrderPopulationByCost
import hu.raven.puppet.logic.evolutionary.common.boost.Boost
import hu.raven.puppet.logic.evolutionary.common.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.evolutionary.common.iteration.EvolutionaryIteration
import hu.raven.puppet.logic.evolutionary.genetic.CrossOvers
import hu.raven.puppet.logic.evolutionary.genetic.SelectSurvivors
import hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.evolutionary.genetic.mutatechildren.MutateChildren

data class GeneticAlgorithmSetup(
    override val initialize: InitializeAlgorithm,

    override val iteration: EvolutionaryIteration,

    override val cost: CalculateCost,
    override val costOfEdge: CalculateCostOfEdge,
    override val costOfObjective: CalculateCostOfObjective,

    override val initializePopulation: InitializePopulation,
    override val orderByCost: OrderPopulationByCost,
    override val boost: Boost,
    val selection: SelectSurvivors,
    val crossover: CrossOvers,
    val crossoverOperator: CrossOverOperator,
    val mutate: MutateChildren
) : SEvolutionaryAlgorithmSetup(
    initialize,
    iteration,
    cost,
    costOfEdge,
    costOfObjective,
    initializePopulation,
    orderByCost,
    boost
)