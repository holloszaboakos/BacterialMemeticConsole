package hu.raven.puppet.logic.evolutionary.bacterial.mutation

import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.evolutionary.bacterial.mutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.Statistics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class BacterialMutationOnAllAndFullCoverRandomOrder<S : ISpecimenRepresentation>(
    val mutationPercentage: Float,
    override val algorithm: BacterialAlgorithm<S>
) : BacterialMutation<S> {

    val statistics: Statistics<S> by inject(Statistics::class.java)
    val calculateCostOf: CalculateCost<S> by inject(CalculateCost::class.java)
    val mutationOperator: BacterialMutationOperator<S> by inject(BacterialMutationOperator::class.java)

    var order = intArrayOf()

    override suspend fun invoke(
    ) = withContext(Dispatchers.Default) {
        if (order.isEmpty()) {
            order = (0 until algorithm.population.first().permutationSize - algorithm.cloneSegmentLength)
                .shuffled()
                .toIntArray()
        }


        algorithm.run {
            val selectedCount = ((population.size - 1) * mutationPercentage).toInt()

            val populationRandomized = population.slice(1 until population.size)
                .shuffled()
                .slice(0 until selectedCount)
                .toMutableList()
                .apply { add(0, population.first()) }

            statistics.mutationStepCall++

            populationRandomized
                .forEachIndexed { index, specimen ->
                    launch {
                        val oldCost = specimen.cost

                        mutateSpecimen(specimen)

                        if (specimen.cost == oldCost) {
                            return@launch
                        }

                        synchronized(statistics) {
                            statistics.mutationCall++
                            statistics.mutationImprovementCountOnAll++

                            if (index == 0) {
                                statistics.mutationImprovementCountOnBest++
                            }
                        }
                    }
                }
        }
    }

    private suspend fun BacterialAlgorithm<S>.mutateSpecimen(
        specimen: S
    ) {
        repeat(cloneCycleCount) { cycleIndex ->
            val selectedPosition = order[(iteration * cloneCycleCount + cycleIndex) % order.size]
            val selectedPositions =
                (selectedPosition until selectedPosition + cloneSegmentLength).toList().toIntArray()

            val selectedElements = IntArray(cloneSegmentLength) {
                specimen[selectedPositions[it]]
            }

            val clones = MutableList(cloneCount + 1) { subSolutionFactory.copy(specimen) }
            withContext(Dispatchers.Default) {
                clones
                    .slice(1 until clones.size)
                    .forEach { clone ->
                        launch {
                            mutationOperator(
                                clone,
                                selectedPositions,
                                selectedElements
                            )
                        }
                    }
            }

            calcCostOfEachAndSort(clones)

            specimen.setData(clones.first().getData())
            specimen.cost = clones.first().cost
        }
    }
}