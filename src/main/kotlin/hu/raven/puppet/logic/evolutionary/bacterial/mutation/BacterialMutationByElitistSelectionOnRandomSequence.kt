package hu.raven.puppet.logic.evolutionary.bacterial.mutation

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.Statistics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import kotlin.random.Random

class BacterialMutationByElitistSelectionOnRandomSequence(
    val mutationPercentage: Float
) : BacterialMutation {

    val statistics: Statistics by inject(Statistics::class.java)

    override suspend fun <S : ISpecimenRepresentation> invoke(
        algorithm: BacterialAlgorithm<S>
    ): Unit = withContext(Dispatchers.Default) {
        algorithm.run {
            statistics.mutationStepCall++

            population.mapIndexed { index, specimen ->
                launch {
                    if (index != 0 && Random.nextFloat() > mutationPercentage) {
                        return@launch
                    }
                    synchronized(statistics) {
                        statistics.mutationCall++
                    }
                    val oldCost = specimen.cost

                    mutateSpecimen(specimen)

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
            }
        }
    }

    private fun <S : ISpecimenRepresentation> BacterialAlgorithm<S>.mutateSpecimen(
        specimen: S
    ) {
        repeat(cloneCycleCount) {
            val selectedPositions = selectSegment(specimen)
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