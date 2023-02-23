package hu.raven.puppet.logic.step.evolutionary.bacterial.mutationonspecimen

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.common.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.evolutionary.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.evolutionary.bacterial.mutationoperator.BacterialMutationOperator
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.utility.inject

sealed class MutationOnSpecimen<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> :
    EvolutionaryAlgorithmStep<S, C>() {

    protected val cloneCount: Int by inject(AlgorithmParameters.CLONE_COUNT)
    protected val cloneSegmentLength: Int by inject(AlgorithmParameters.CLONE_SEGMENT_LENGTH)
    protected val cloneCycleCount: Int by inject(AlgorithmParameters.CLONE_CYCLE_COUNT)

    protected val mutationOperator: BacterialMutationOperator<S, C> by inject()

    fun calcCostOfEachAndSort(clones: MutableList<S>) {
        val calculateCostOf: CalculateCost<S, C> by inject()

        clones
            .onEach { calculateCostOf(it) }
            .sortBy { it.costOrException().value.toDouble() }
    }

    abstract operator fun invoke(specimen: S): StepEfficiencyData
}