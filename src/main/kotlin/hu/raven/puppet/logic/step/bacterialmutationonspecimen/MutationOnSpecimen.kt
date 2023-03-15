package hu.raven.puppet.logic.step.bacterialmutationonspecimen

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.utility.inject

sealed class MutationOnSpecimen<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    EvolutionaryAlgorithmStep<S, C>() {

    protected val cloneCount: Int by inject(AlgorithmParameters.CLONE_COUNT)
    protected val cloneSegmentLength: Int by inject(AlgorithmParameters.CLONE_SEGMENT_LENGTH)
    protected val cloneCycleCount: Int by inject(AlgorithmParameters.CLONE_CYCLE_COUNT)

    protected val mutationOperator: BacterialMutationOperator<S, C> by inject()
    protected val calculateCostOf: CalculateCost<S, C> by inject()

    fun calcCostOfEachAndSort(clones: MutableList<S>) {

        clones
            .onEach { calculateCostOf(it) }
            .sortBy { it.costOrException().value }
    }

    abstract operator fun invoke(specimen: S): StepEfficiencyData
}