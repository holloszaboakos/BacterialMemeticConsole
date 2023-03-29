package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Opt2StepWithPerSpecimenProgressMemoryAndRandomOrder<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val taskHolder: VRPTaskHolder,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val sizeOfPopulation: Int,
    override val iterationLimit: Int,
    override val geneCount: Int,
    override val calculateCostOf: CalculateCost<S, C>
) :
    BoostOperator<S, C>() {

    var lastPositionPerSpecimen = arrayOf<Pair<Int, Int>>()
    var shuffler = intArrayOf()

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: S): StepEfficiencyData {
        var spentBudget = 0L
        val oldCost = specimen.costOrException()
        val spentTime = measureTime {
            if (lastPositionPerSpecimen.isEmpty()) {
                lastPositionPerSpecimen = Array(sizeOfPopulation) { Pair(0, 1) }
            }
            if (shuffler.isEmpty()) {
                shuffler = (0 until algorithmState.population.first().permutationSize)
                    .shuffled()
                    .toIntArray()
            }

            val bestCost = specimen.cost
            var improved = false

            var lastPosition = lastPositionPerSpecimen[specimen.id]

            outer@ for (firstIndexIndex in lastPosition.first until taskHolder.task.costGraph.objectives.size - 1) {
                val firstIndex = shuffler[firstIndexIndex]
                val secondIndexStart =
                    if (firstIndexIndex == lastPosition.first) lastPosition.second
                    else firstIndexIndex + 1
                for (secondIndexIndex in secondIndexStart until algorithmState.population.first().permutationSize) {
                    val secondIndex = shuffler[secondIndexIndex]
                    specimen.swapGenes(firstIndex, secondIndex)
                    calculateCostOf(specimen)
                    spentBudget++

                    if (specimen.costOrException() >= bestCost!!) {
                        specimen.swapGenes(firstIndex, secondIndex)
                        specimen.cost = bestCost
                        continue
                    }

                    improved = true
                    lastPosition = Pair(firstIndexIndex, secondIndexIndex)
                    break@outer
                }
            }

            if (!improved) {
                lastPosition = Pair(0, 1)
            }
            lastPositionPerSpecimen[specimen.id] = lastPosition
        }

        return StepEfficiencyData(
            spentTime = spentTime,
            spentBudget = spentBudget,
            improvementCountPerRun = if (specimen.costOrException() < oldCost) 1 else 0,
            improvementPercentagePerBudget =
            if (specimen.costOrException() < oldCost)
                (Fraction.new(1) - (specimen.costOrException().value / oldCost.value)) / spentBudget
            else
                Fraction.new(0)
        )
    }
}