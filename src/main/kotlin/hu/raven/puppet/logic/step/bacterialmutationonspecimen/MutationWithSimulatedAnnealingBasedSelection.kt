package hu.raven.puppet.logic.step.bacterialmutationonspecimen

import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlin.math.exp
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class MutationWithSimulatedAnnealingBasedSelection<C : PhysicsUnit<C>>(
    override val algorithmState: EvolutionaryAlgorithmState<C>,
    override val parameters: BacterialMutationParameterProvider<C>,
    override val mutationOperator: BacterialMutationOperator<C>,
    override val calculateCostOf: CalculateCost<C>,
    override val selectSegment: SelectSegment<C>
) : MutationOnSpecimen<C>() {
    private val randomizer: IntArray by lazy {
        (0 until parameters.cloneSegmentLength)
            .shuffled()
            .toIntArray()
    }

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: OnePartRepresentation<C>): StepEfficiencyData = algorithmState.run {
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
        specimen: OnePartRepresentation<C>,
        selectedSegment: Segment
    ): MutableList<OnePartRepresentation<C>> {
        val clones = MutableList(parameters.cloneCount + 1) { specimen.copy() }
        clones
            .slice(1 until clones.size)
            .forEach { clone ->
                mutationOperator(clone, selectedSegment)
            }
        return clones
    }

    private fun <C : PhysicsUnit<C>> loadDataToSpecimen(
        specimen: OnePartRepresentation<C>,
        clones: MutableList<OnePartRepresentation<C>>,
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