package hu.raven.puppet.logic.operator.genetransfer_operator

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesBigger
import hu.raven.puppet.logic.operator.bacteriophage_transduction_operator.BacteriophageTransductionOperator
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.AlgorithmSolution

import hu.raven.puppet.model.state.BacteriophageAlgorithmState

class GeneTransferOperatorWithBacteriophageTransduction<S : AlgorithmSolution<Permutation, S>>(
    override val calculateCostOf: CalculateCost<Permutation, *>,
    override val geneTransferSegmentLength: Int,
    val geneTransferOperator: GeneTransferOperator<Permutation, S>,
    private val getActualAlgorithmState: () -> BacteriophageAlgorithmState<*>,
    private val bacteriophageTransductionOperator: BacteriophageTransductionOperator,
) : GeneTransferOperator<Permutation, S>() {
    override fun invoke(source: S, target: S) {
        val algorithmState = getActualAlgorithmState()
        val oldCost = target.costOrException()
        val oldPermutation = target.representation.clone()
        geneTransferOperator(source, target)

        if (
            target.costOrException() dominatesBigger oldCost &&
            algorithmState.virusPopulation.activeCount != algorithmState.virusPopulation.poolSize
        ) {
            val bacteriophage = algorithmState.virusPopulation.inactivesAsSequence().first()
            bacteriophageTransductionOperator(
                oldPermutation,
                target.representation,
                bacteriophage.value
            )
            algorithmState.virusPopulation.activate(bacteriophage.index)
        }
    }
}