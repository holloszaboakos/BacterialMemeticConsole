package hu.raven.puppet.logic.evolutionary.bacterial.mutation

import hu.raven.puppet.logic.common.logging.DoubleLogger
import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.evolutionary.bacterial.mutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import kotlin.math.exp
import kotlin.random.Random.Default.nextFloat
import kotlin.random.Random.Default.nextInt

class BacterialMutationWithSimulatedAnnealingThatTouchesAllGenesAndContinuesSegment<S : ISpecimenRepresentation>(
    val mutationPercentage: Float,
    override val algorithm: BacterialAlgorithm<S>
) : BacterialMutation<S> {

    private val statistics: BacterialAlgorithmStatistics by inject(BacterialAlgorithmStatistics::class.java)
    val logger: DoubleLogger by inject(DoubleLogger::class.java)
    val calculateCostOf: CalculateCost<S> by inject(CalculateCost::class.java)
    val mutationOperator: BacterialMutationOperator<S> by inject(BacterialMutationOperator::class.java)

    override suspend fun invoke() = withContext(Dispatchers.Default) {
        algorithm.run {
            logger(simulatedAnnealingHeat(iteration, iterationLimit).toString())

            val randomStartPosition =
                nextInt(cloneSegmentLength)

            population.forEachIndexed { index, specimen ->
                launch {
                    val oldCost = specimen.cost

                    mutateSpecimen(
                        specimen,
                        randomStartPosition,
                        index != 0
                    )

                    if (specimen.cost == oldCost) {
                        return@launch
                    }


                    synchronized(statistics) {
                        statistics.mutationImprovement = statistics.mutationImprovement.run {
                            copy(improvementCountTotal = improvementCountTotal + 1)
                        }

                        if (index == 0) {
                            statistics.mutationOnBestImprovement = statistics.mutationOnBestImprovement.run {
                                copy(improvementCountTotal = improvementCountTotal + 1)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun BacterialAlgorithm<S>.mutateSpecimen(
        specimen: S,
        randomStartPosition: Int,
        doSimulatedAnnealing: Boolean
    ) {
        repeat(cloneCycleCount) { cycleCount ->

            val segmentPosition = randomStartPosition + cycleCount * cloneSegmentLength
            val selectedPositions = IntArray(cloneSegmentLength) { segmentPosition + it }
            val selectedElements = IntArray(cloneSegmentLength) {
                specimen[selectedPositions[it]]
            }

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
        }
    }

    private fun BacterialAlgorithm<S>.generateClones(
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

    private fun <S : ISpecimenRepresentation> BacterialAlgorithm<S>.loadDataToSpecimen(
        specimen: S,
        clones: MutableList<S>,
        doSimulatedAnnealing: Boolean
    ) {
        if (!doSimulatedAnnealing || nextFloat() > simulatedAnnealingHeat(iteration, iterationLimit)) {
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