package hu.raven.puppet.logic.operator.bacterialmutationonspecimen

import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesBigger
import hu.raven.puppet.logic.operator.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.operator.bacteriophagetransduction.BacteriophageTransductionOperator
import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.logic.operator.selectsegments.SelectSegments
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost
import hu.raven.puppet.model.state.BacteriophageAlgorithmState

class MutationOnSpecimenWithBacteriophageTransduction(
    private val mutationOnSpecimen: MutationOnSpecimen,
    private val bacteriophageTransductionOperator: BacteriophageTransductionOperator,
    private val getActualAlgorithmState: () -> BacteriophageAlgorithmState
) : MutationOnSpecimen() {
    override val mutationOperator: BacterialMutationOperator
        get() = throw Exception("Should not be used!")
    override val calculateCostOf: CalculateCost
        get() = throw Exception("Should not be used!")
    override val selectSegments: SelectSegments
        get() = throw Exception("Should not be used!")
    override val cloneCount: Int
        get() = throw Exception("Should not be used!")
    override val cloneCycleCount: Int
        get() = throw Exception("Should not be used!")

    override fun invoke(specimenWithIndex: IndexedValue<OnePartRepresentationWithCost>, iteration: Int) {
        val algorithmState = getActualAlgorithmState()
        val oldCost = specimenWithIndex.value.costOrException()
        val oldPermutation = specimenWithIndex.value.permutation.clone()
        mutationOnSpecimen(specimenWithIndex, iteration)
        if (
            specimenWithIndex.value.costOrException() dominatesBigger oldCost &&
            algorithmState.virusPopulation.activeCount != algorithmState.virusPopulation.poolSize
        ) {
            val bacteriophage = algorithmState.virusPopulation.inactivesAsSequence().first()
            bacteriophageTransductionOperator(
                oldPermutation,
                specimenWithIndex.value.permutation,
                bacteriophage
            )
            algorithmState.virusPopulation.activate(bacteriophage.id)
        }
    }
}