package hu.raven.puppet.logic.step.evolutionary.bacterial.mutationonspecimen

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import kotlin.math.exp
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class MutationOnSpecimenWithRandomContinuousSegmentAndFullCoverAndSimulatedAnnealing<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> :
    MutationOnSpecimen<S, C>() {
    private val randomizer: IntArray by lazy {
        (0 until cloneSegmentLength)
            .shuffled()
            .toIntArray()
    }

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: S): StepEfficiencyData = algorithmState.run {
        var impruvement = false
        val oldSpecimenCost = specimen.cost
        val duration = measureTime {
            val doSimulatedAnnealing = specimen != population.first()
            repeat(cloneCycleCount) { cycleCount ->
                val randomStartPosition = randomizer[iteration % randomizer.size]
                val segmentPosition = randomStartPosition + cycleCount * cloneSegmentLength
                val selectedPositions = IntArray(cloneSegmentLength) { segmentPosition + it }
                val selectedElements = selectedPositions
                    .map { specimen[it] }
                    .toIntArray()

                val clones = generateClones(
                    specimen,
                    selectedPositions,
                    selectedElements
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

        val spentBudget = (cloneCount + 1) * cloneCycleCount.toLong()
        StepEfficiencyData(
            spentTime = duration,
            spentBudget = spentBudget,
            improvementCountPerRun = if (impruvement) 1 else 0,
            improvementPercentagePerBudget =
            if (impruvement)
                (1 - (specimen.cost!!.value.toDouble() / oldSpecimenCost!!.value.toDouble())) / spentBudget
            else
                0.0
        )
    }

    private fun generateClones(
        specimen: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ): MutableList<S> {
        val clones = MutableList(cloneCount + 1) { subSolutionFactory.copy(specimen) }
        clones
            .slice(1 until clones.size)
            .forEach { clone ->
                mutationOperator(
                    clone,
                    selectedPositions,
                    selectedElements
                )
            }
        return clones
    }

    private fun <S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> loadDataToSpecimen(
        specimen: S,
        clones: MutableList<S>,
        doSimulatedAnnealing: Boolean
    ) {
        if (!doSimulatedAnnealing ||
            Random.nextFloat() > simulatedAnnealingHeat(
                algorithmState.iteration,
                iterationLimit
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