package hu.raven.puppet.logic.operator.boost_operator

import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesBigger
import hu.raven.puppet.logic.operator.bacteriophage_transduction_operator.BacteriophageTransductionOperator
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost
import hu.raven.puppet.model.state.BacteriophageAlgorithmState

class BoostOperatorWithBacteriophageTransduction<O : OnePartRepresentationWithCost>(
    val boostOperator: BoostOperator<O>,
    private val bacteriophageTransductionOperator: BacteriophageTransductionOperator,
    private val getActualAlgorithmState: () -> BacteriophageAlgorithmState
) :BoostOperator<O>() {
    override val calculateCostOf: CalculateCost
        get() = throw Exception("Should not be used!")
    override fun invoke(specimen:O) {
        val algorithmState = getActualAlgorithmState()
        val oldCost = specimen.costOrException()
        val oldPermutation = specimen.permutation.clone()
        boostOperator(specimen)

        if (
            specimen.costOrException() dominatesBigger oldCost &&
            algorithmState.virusPopulation.activeCount != algorithmState.virusPopulation.poolSize
        ) {
            val bacteriophage = algorithmState.virusPopulation.inactivesAsSequence().first()
            bacteriophageTransductionOperator(
                oldPermutation,
                specimen.permutation,
                bacteriophage
            )
            algorithmState.virusPopulation.activate(bacteriophage.id)
        }
    }
}