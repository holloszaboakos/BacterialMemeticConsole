package hu.raven.puppet.logic.operator.bacterial_mutation_on_specimen

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesBigger
import hu.raven.puppet.logic.operator.bacterial_mutation_operator.BacterialMutationOperator
import hu.raven.puppet.logic.operator.bacteriophage_transduction_operator.BacteriophageTransductionOperator
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.logic.operator.select_segments.SelectSegments
import hu.raven.puppet.model.solution.AlgorithmSolution
import hu.raven.puppet.model.state.BacteriophageAlgorithmState

class MutationOnSpecimenWithBacteriophageTransduction<A : AlgorithmSolution<Permutation, A>>(
    private val mutationOnSpecimen: MutationOnSpecimen<Permutation, A>,
    private val bacteriophageTransductionOperator: BacteriophageTransductionOperator,
    private val getActualAlgorithmState: () -> BacteriophageAlgorithmState<Permutation>
) : MutationOnSpecimen<Permutation, A>() {
    override val mutationOperator: BacterialMutationOperator<Permutation, A>
        get() = throw Exception("Should not be used!")
    override val calculateCostOf: CalculateCost<Permutation, *>
        get() = throw Exception("Should not be used!")
    override val selectSegments: SelectSegments
        get() = throw Exception("Should not be used!")
    override val cloneCount: Int
        get() = throw Exception("Should not be used!")
    override val cloneCycleCount: Int
        get() = throw Exception("Should not be used!")

    override fun invoke(specimenWithIndex: IndexedValue<A>, iteration: Int) {
        val algorithmState = getActualAlgorithmState()
        val oldCost = specimenWithIndex.value.costOrException()
        val oldPermutation = specimenWithIndex.value.representation.clone()
        mutationOnSpecimen(specimenWithIndex, iteration)
        if (
            specimenWithIndex.value.costOrException() dominatesBigger oldCost &&
            algorithmState.virusPopulation.activeCount != algorithmState.virusPopulation.poolSize
        ) {
            val bacteriophage = algorithmState.virusPopulation.inactivesAsSequence().first()
            bacteriophageTransductionOperator(
                oldPermutation,
                specimenWithIndex.value.representation,
                bacteriophage.value
            )
            algorithmState.virusPopulation.activate(bacteriophage.index)
        }
    }
}