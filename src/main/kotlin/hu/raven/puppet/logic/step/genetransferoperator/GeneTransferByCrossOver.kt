package hu.raven.puppet.logic.step.genetransferoperator

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class GeneTransferByCrossOver<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val taskHolder: VRPTaskHolder,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val sizeOfPopulation: Int,
    override val iterationLimit: Int,
    override val geneCount: Int,
    override val calculateCostOf: CalculateCost<S, C>,
    override val geneTransferSegmentLength: Int,
    val crossOverOperator: CrossOverOperator<S, C>,
) : GeneTransferOperator<S, C>() {


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
                improvementPercentagePerBudget = Fraction.new(1) - (target.costOrException().value / oldCost!!.value)
            )
        }

        return StepEfficiencyData(
            spentTime = spentTime,
            spentBudget = 1
        )
    }
}