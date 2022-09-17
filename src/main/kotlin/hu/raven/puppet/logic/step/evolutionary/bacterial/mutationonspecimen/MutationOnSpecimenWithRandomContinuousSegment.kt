package hu.raven.puppet.logic.step.evolutionary.bacterial.mutationonspecimen

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.evolutionary.bacterial.mutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.evolutionary.bacterial.selectsegment.SelectSegment
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.utility.inject
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class MutationOnSpecimenWithRandomContinuousSegment<S : ISpecimenRepresentation> : MutationOnSpecimen<S>() {
    private val selectSegment: SelectSegment<S> by inject()

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: S): StepEfficiencyData = algorithmState.run {
        var impruvement = false
        val oldSpecimenCost = specimen.cost
        val duration = measureTime {
            repeat(cloneCycleCount) {
                val selectedPositions = selectSegment(specimen)
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
                (1 - (specimen.cost / oldSpecimenCost)) / spentBudget
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