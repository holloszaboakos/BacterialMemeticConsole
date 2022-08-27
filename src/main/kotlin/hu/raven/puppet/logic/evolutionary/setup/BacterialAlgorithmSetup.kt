package hu.raven.puppet.logic.evolutionary.setup

import hu.raven.puppet.logic.common.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.common.steps.calculatecostofedge.CalculateCostOfEdge
import hu.raven.puppet.logic.common.steps.calculatecostofobjective.CalculateCostOfObjective
import hu.raven.puppet.logic.evolutionary.common.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.evolutionary.common.OrderPopulationByCost
import hu.raven.puppet.logic.evolutionary.common.iteration.EvolutionaryIteration
import hu.raven.puppet.logic.evolutionary.common.boost.Boost
import hu.raven.puppet.logic.evolutionary.bacterial.mutation.BacterialMutation
import hu.raven.puppet.logic.evolutionary.bacterial.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.logic.evolutionary.bacterial.genetransfer.GeneTransfer
import hu.raven.puppet.logic.evolutionary.bacterial.mutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.evolutionary.bacterial.selectsegment.SelectSegment

class BacterialAlgorithmSetup(
    override val initialize: InitializeAlgorithm,

    override val iteration: EvolutionaryIteration,

    override val cost: CalculateCost,
    override val costOfEdge: CalculateCostOfEdge,
    override val costOfObjective: CalculateCostOfObjective,

    override val initializePopulation: InitializePopulation,
    override val orderByCost: OrderPopulationByCost,
    override val boost: Boost,
    val geneTransfer: GeneTransfer,
    val geneTransferOperator: GeneTransferOperator,
    val mutate: BacterialMutation,
    val mutationOperator: BacterialMutationOperator,
    val selectSegment: SelectSegment
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