package hu.raven.puppet.logic.operator.boost_operator

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesBigger
import hu.raven.puppet.logic.operator.bacteriophage_transduction_operator.BacteriophageTransductionOperator
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.AlgorithmSolution
import hu.raven.puppet.model.state.BacteriophageAlgorithmState

class BoostOperatorWithBacteriophageTransduction<S : AlgorithmSolution<Permutation, S>>(
    val boostOperator: BoostOperator<Permutation, S>,
    private val bacteriophageTransductionOperator: BacteriophageTransductionOperator,
    private val getActualAlgorithmState: () -> BacteriophageAlgorithmState<*>
) : BoostOperator<Permutation, S>() {
    override val calculateCostOf: CalculateCost<Permutation, *>
        get() = throw Exception("Should not be used!")

    override fun invoke(specimen: S) {
        val algorithmState = getActualAlgorithmState()
        val oldCost = specimen.costOrException()
        val oldPermutation = specimen.representation.clone()
        boostOperator(specimen)

        if (
            specimen.costOrException() dominatesBigger oldCost &&
            algorithmState.virusPopulation.activeCount != algorithmState.virusPopulation.poolSize
        ) {
            val bacteriophage = algorithmState.virusPopulation.inactivesAsSequence().first()
            bacteriophageTransductionOperator(
                oldPermutation,
                specimen.representation,
                bacteriophage.value
            )
            algorithmState.virusPopulation.activate(bacteriophage.index)
        }
    }
}