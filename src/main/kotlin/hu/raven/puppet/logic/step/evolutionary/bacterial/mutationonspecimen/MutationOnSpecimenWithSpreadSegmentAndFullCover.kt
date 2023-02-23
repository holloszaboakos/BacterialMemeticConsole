package hu.raven.puppet.logic.step.evolutionary.bacterial.mutationonspecimen

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class MutationOnSpecimenWithSpreadSegmentAndFullCover<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> :
    MutationOnSpecimen<S, C>() {
    private val randomPermutation: IntArray by lazy {
        IntArray(geneCount) { it }
            .apply { shuffle() }
    }

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: S): StepEfficiencyData = algorithmState.run {
        var impruvement = false
        val oldSpecimenCost = specimen.cost
        val duration = measureTime {
            repeat(cloneCycleCount) { cycleCount ->
                val segmentStart = cycleCount * cloneSegmentLength
                val segmentEnd = (cycleCount + 1) * cloneSegmentLength
                val selectedPositions = randomPermutation
                    .slice(segmentStart until segmentEnd)
                    .sortedBy { it }
                    .toIntArray()
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
                (1 - (specimen.costOrException().value.toDouble() / oldSpecimenCost!!.value.toDouble())) / spentBudget
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