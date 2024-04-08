package hu.raven.puppet.logic.operator.genetransfer_operator

import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesBigger
import hu.raven.puppet.logic.operator.bacteriophage_transduction_operator.BacteriophageTransductionOperator
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost
import hu.raven.puppet.model.state.BacteriophageAlgorithmState

class GeneTransferOperatorWithBacteriophageTransduction<T>(
    override val calculateCostOf: CalculateCost<T>,
    override val geneTransferSegmentLength: Int,
    val geneTransferOperator: GeneTransferOperator<T>,
    private val getActualAlgorithmState: () -> BacteriophageAlgorithmState<*>,
    private val bacteriophageTransductionOperator: BacteriophageTransductionOperator,
) : GeneTransferOperator<T>() {
    override fun invoke(source: OnePartRepresentationWithCost, target: OnePartRepresentationWithCost) {
        val algorithmState = getActualAlgorithmState()
        val oldCost = target.costOrException()
        val oldPermutation = target.permutation.clone()
        geneTransferOperator(source, target)

        if (
            target.costOrException() dominatesBigger oldCost &&
            algorithmState.virusPopulation.activeCount != algorithmState.virusPopulation.poolSize
        ) {
            val bacteriophage = algorithmState.virusPopulation.inactivesAsSequence().first()
            bacteriophageTransductionOperator(
                oldPermutation,
                target.permutation,
                bacteriophage
            )
            algorithmState.virusPopulation.activate(bacteriophage.id)
        }
    }
}