package hu.raven.puppet.logic.step.bacterialmutationonspecimen

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation

sealed class MutationOnSpecimen<C : PhysicsUnit<C>> :
    EvolutionaryAlgorithmStep<C>() {

    abstract override val parameters: BacterialMutationParameterProvider<C>
    protected abstract val mutationOperator: BacterialMutationOperator<C>
    protected abstract val calculateCostOf: CalculateCost<C>
    protected abstract val selectSegment: SelectSegment<C>

    fun calcCostOfEachAndSort(clones: MutableList<OnePartRepresentation<C>>) {
        clones
            .onEach { calculateCostOf(it) }
            .sortBy { it.costOrException().value }
    }

    abstract operator fun invoke(specimen: OnePartRepresentation<C>): StepEfficiencyData
}