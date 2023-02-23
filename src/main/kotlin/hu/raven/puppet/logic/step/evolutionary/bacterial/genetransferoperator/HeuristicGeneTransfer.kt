package hu.raven.puppet.logic.step.evolutionary.bacterial.genetransferoperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.evolutionary.genetic.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.inject
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class GeneTransferByCrossOver<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : GeneTransferOperator<S, C>() {

    val crossOverOperator: CrossOverOperator<S, C> by inject()

    @OptIn(ExperimentalTime::class)
    override fun invoke(source: S, target: S): StepEfficiencyData {
        val child = subSolutionFactory.copy(target)

        val spentTime = measureTime {
            crossOverOperator(
                Pair(source, target),
                child
            )
        }

        calculateCostOf(child)

        if (child.costOrException() < target.costOrException()) {
            target.setData(child.getData())
            val oldCost = target.cost
            target.cost = child.cost
            return StepEfficiencyData(
                spentTime = spentTime,
                spentBudget = 1,
                improvementCountPerRun = 1,
                improvementPercentagePerBudget = 1 - (target.costOrException().value.toDouble() / oldCost!!.value.toDouble())
            )
        }

        return StepEfficiencyData(
            spentTime = spentTime,
            spentBudget = 1
        )
    }
}