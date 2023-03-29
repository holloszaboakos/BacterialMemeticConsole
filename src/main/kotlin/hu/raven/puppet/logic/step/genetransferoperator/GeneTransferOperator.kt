package hu.raven.puppet.logic.step.genetransferoperator

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation


sealed class GeneTransferOperator<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    EvolutionaryAlgorithmStep<S, C>() {
    abstract val calculateCostOf: CalculateCost<S, C>
    abstract val geneTransferSegmentLength: Int

    abstract operator fun invoke(
        source: S,
        target: S
    ): StepEfficiencyData
}