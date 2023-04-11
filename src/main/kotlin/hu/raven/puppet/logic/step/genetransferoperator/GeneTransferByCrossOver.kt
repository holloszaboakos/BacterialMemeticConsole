package hu.raven.puppet.logic.step.genetransferoperator

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem

import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class GeneTransferByCrossOver<C : PhysicsUnit<C>>(
    override val calculateCostOf: CalculateCost<C>,
    override val geneTransferSegmentLength: Int,
    val crossOverOperator: CrossOverOperator<C>,
) : GeneTransferOperator<C>() {
    @OptIn(ExperimentalTime::class)
    override fun invoke(
        source: PoolItem<OnePartRepresentationWithIteration<C>>,
        target: PoolItem<OnePartRepresentationWithIteration<C>>
    ): StepEfficiencyData {
        val child = target.copy()

        val spentTime = measureTime {
            crossOverOperator(
                Pair(
                    source.content.permutation,
                    target.content.permutation
                ),
                child.content.permutation
            )
        }

        calculateCostOf(child.content)

        if (child.content.costOrException() < target.content.costOrException()) {
            child.content.permutation.forEachIndexed { index, value ->
                target.content.permutation[index] = value
            }
            val oldCost = target.content.cost
            target.content.cost = child.content.cost
            return StepEfficiencyData(
                spentTime = spentTime,
                spentBudget = 1,
                improvementCountPerRun = 1,
                improvementPercentagePerBudget = Fraction.new(1) - (target.content.costOrException().value / oldCost!!.value)
            )
        }

        return StepEfficiencyData(
            spentTime = spentTime,
            spentBudget = 1
        )
    }
}