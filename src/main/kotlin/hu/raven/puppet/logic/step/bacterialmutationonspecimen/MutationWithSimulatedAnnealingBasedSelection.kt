package hu.raven.puppet.logic.step.bacterialmutationonspecimen

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import kotlin.math.exp
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class MutationWithSimulatedAnnealingBasedSelection<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,

    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: BacterialMutationParameterProvider<S, C>,
    override val mutationOperator: BacterialMutationOperator<S, C>,
    override val calculateCostOf: CalculateCost<S, C>,
    override val selectSegment: SelectSegment<S, C>
) : MutationOnSpecimen<S, C>() {
    private val randomizer: IntArray by lazy {
        (0 until parameters.cloneSegmentLength)
            .shuffled()
            .toIntArray()
    }

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: S): StepEfficiencyData = algorithmState.run {
        var impruvement = false
        val oldSpecimenCost = specimen.cost
        val duration = measureTime {
            val doSimulatedAnnealing = specimen != population.first()
            repeat(parameters.cloneCycleCount) { cycleIndex ->

                val clones = generateClones(
                    specimen,
                    selectSegment(specimen, cycleIndex, parameters.cloneCycleCount)
                )

                calcCostOfEachAndSort(clones)

                loadDataToSpecimen(
                    specimen,
                    clones,
                    doSimulatedAnnealing
                )

                if (clones.first().cost != oldSpecimenCost) {
                    impruvement = true
                }
            }
        }

        val spentBudget = (parameters.cloneCount + 1) * parameters.cloneCycleCount.toLong()
        StepEfficiencyData(
            spentTime = duration,
            spentBudget = spentBudget,
            improvementCountPerRun = if (impruvement) 1 else 0,
            improvementPercentagePerBudget =
            if (impruvement)
                (Fraction.new(1) - (specimen.costOrException().value / oldSpecimenCost!!.value)) / spentBudget
            else
                Fraction.new(0)
        )
    }

    private fun generateClones(
        specimen: S,
        selectedSegment: Segment
    ): MutableList<S> {
        val clones = MutableList(parameters.cloneCount + 1) { subSolutionFactory.copy(specimen) }
        clones
            .slice(1 until clones.size)
            .forEach { clone ->
                mutationOperator(clone, selectedSegment)
            }
        return clones
    }

    private fun <S : SolutionRepresentation<C>, C : PhysicsUnit<C>> loadDataToSpecimen(
        specimen: S,
        clones: MutableList<S>,
        doSimulatedAnnealing: Boolean
    ) {
        if (!doSimulatedAnnealing ||
            Random.nextFloat() > simulatedAnnealingHeat(
                algorithmState.iteration,
                parameters.iterationLimit
            )
        ) {
            specimen.setData(clones.first().getData())
            specimen.cost = clones.first().cost
            return
        }

        specimen.setData(clones[1].getData())
        specimen.cost = clones[1].cost
    }

    private fun simulatedAnnealingHeat(iteration: Int, divider: Int): Float {
        return 1 / (1 + exp(iteration.toFloat() / divider))
    }
}