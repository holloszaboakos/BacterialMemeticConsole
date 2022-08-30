package hu.raven.puppet.logic.evolutionary.bacterial.mutation

import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.evolutionary.bacterial.mutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import kotlin.random.Random
import kotlin.random.Random.Default.nextInt

class BacterialMutationWithElitistSelectionThatTouchesAllGenesAndContinuesSegment<S : ISpecimenRepresentation>(
    val mutationPercentage: Float,
    override val algorithm: BacterialAlgorithm<S>
) : BacterialMutation<S> {

    private val statistics: BacterialAlgorithmStatistics by inject(BacterialAlgorithmStatistics::class.java)
    val calculateCostOf: CalculateCost<S> by inject(CalculateCost::class.java)
    val mutationOperator: BacterialMutationOperator<S> by inject(BacterialMutationOperator::class.java)


    override suspend fun invoke() = withContext(Dispatchers.Default) {
        algorithm.run {
            statistics.mutationStepCall++

            val randomStartPosition =
                nextInt(cloneSegmentLength)

            population.mapIndexed { index, specimen ->
                launch {
                    if (index != 0 && Random.nextFloat() > mutationPercentage) {
                        return@launch
                    }
                    synchronized(statistics) {
                        statistics.mutationCall++
                    }
                    val oldCost = specimen.cost

                    mutateSpecimen(specimen, randomStartPosition)

                    if (specimen.cost == oldCost) {
                        return@launch
                    }

                    synchronized(statistics) {
                        statistics.mutationImprovementCountOnAll++

                        if (index == 0) {
                            statistics.mutationImprovementCountOnBest++
                        }
                    }
                }
            }.forEach {
                it.join()
            }
        }
    }


    private fun BacterialAlgorithm<S>.mutateSpecimen(
        specimen: S,
        randomStartPosition: Int
    ) {
        repeat(cloneCycleCount) { cycleCount ->

            synchronized(statistics) {
                statistics.mutationCycleCall++
            }
            val segmentPosition =
                (randomStartPosition + cycleCount * cloneSegmentLength)
            val selectedPositions = IntArray(cloneSegmentLength) { segmentPosition + it }
            val selectedElements = IntArray(cloneSegmentLength) {
                specimen[selectedPositions[it]]
            }

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

            calcCostOfEachAndSort(clones)
            specimen.setData(clones.first().getData())
            specimen.cost = clones.first().cost
        }
    }
}