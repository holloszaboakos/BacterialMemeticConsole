package hu.raven.puppet.logic.step.genetransferoperator

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost


import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class GeneTransferByCrossOver<C : PhysicsUnit<C>>(
    override val calculateCostOf: CalculateCost<C>,
    override val geneTransferSegmentLength: Int,
    val crossOverOperator: CrossOverOperator<C>,
) : GeneTransferOperator<C>() {
    @OptIn(ExperimentalTime::class)
    override fun <O : OnePartRepresentationWithCost<C, O>> invoke(
        source: O,
        target: O
    ): StepEfficiencyData {
        val child = target.clone()

        val spentTime = measureTime {
            crossOverOperator(
                Pair(
                    source.permutation,
                    target.permutation
                ),
                child.permutation
            )
        }

        child.cost = calculateCostOf(child)

        if (child.costOrException() < target.costOrException()) {
            child.permutation.forEachIndexed { index, value ->
                target.permutation[index] = value
            }
            val oldCost = target.cost
            target.cost = child.cost
            return StepEfficiencyData(
                spentTime = spentTime,
                spentBudget = 1,
                improvementCountPerRun = 1,
                improvementPercentagePerBudget = Fraction.new(1) - (target.costOrException().value / oldCost!!.value)
            )
        }

        return StepEfficiencyData(
            spentTime = spentTime,
            spentBudget = 1
        )
    }
}