package hu.raven.puppet.logic.step.mutationonspecimen

import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class MutationOnSpecimenWithContinuousSegmentAndFullCoverAndRandomStart<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
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
            repeat(cloneCycleCount) { cycleCount ->
                val randomStartPosition = randomizer[iteration % randomizer.size]
                val segmentPosition =
                    (randomStartPosition + cycleCount * cloneSegmentLength)
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

                if (clones.first().cost != specimen.cost) {
                    impruvement = true
                    specimen.setData(clones.first().getData())
                    specimen.cost = clones.first().cost
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
                (1 - (specimen.costOrException().value / oldSpecimenCost!!.value).toDouble()) / spentBudget
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
}