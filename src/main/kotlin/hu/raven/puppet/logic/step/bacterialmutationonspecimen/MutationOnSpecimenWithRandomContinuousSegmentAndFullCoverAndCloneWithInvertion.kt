package hu.raven.puppet.logic.step.bacterialmutationonspecimen

import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class MutationOnSpecimenWithRandomContinuousSegmentAndFullCoverAndCloneWithInvertion<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    MutationOnSpecimen<S, C>() {
    private val order by lazy {
        (0 until algorithmState.population.first().permutationSize - cloneSegmentLength)
            .shuffled()
            .toIntArray()
    }

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: S): StepEfficiencyData = algorithmState.run {
        var impruvement = false
        val oldSpecimenCost = specimen.cost
        val duration = measureTime {
            repeat(cloneCycleCount) { cycleIndex ->
                val selectedPosition = order[(iteration * cloneCycleCount + cycleIndex) % order.size]
                val selectedPositions =
                    (selectedPosition until selectedPosition + cloneSegmentLength).toList().toIntArray()
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
                (Fraction.new(1) - (specimen.costOrException().value / oldSpecimenCost!!.value)) / spentBudget
            else
                Fraction.new(0)
        )
    }

    private fun generateClones(
        specimen: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ): MutableList<S> {
        val clones = MutableList(cloneCount + 1) { subSolutionFactory.copy(specimen) }

        invertSegment(
            clones[1],
            selectedPositions,
            selectedElements
        )

        clones
            .slice(2 until clones.size)
            .forEach { clone ->
                mutationOperator(
                    clone,
                    selectedPositions,
                    selectedElements
                )
            }
        return clones
    }

    private fun invertSegment(
        specimen: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ) {
        selectedPositions.forEachIndexed { readIndex, writeIndex ->
            specimen[writeIndex] = selectedElements[selectedElements.size - 1 - readIndex]
        }
    }
}