package hu.raven.puppet.logic.step.genetransferoperator

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem


sealed class GeneTransferOperator<C : PhysicsUnit<C>> {
    abstract val calculateCostOf: CalculateCost<C>
    abstract val geneTransferSegmentLength: Int

    abstract operator fun invoke(
        source: PoolItem<OnePartRepresentationWithIteration<C>>,
        target: PoolItem<OnePartRepresentationWithIteration<C>>
    ): StepEfficiencyData
}