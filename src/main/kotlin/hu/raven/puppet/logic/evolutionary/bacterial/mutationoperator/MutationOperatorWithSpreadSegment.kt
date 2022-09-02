package hu.raven.puppet.logic.evolutionary.bacterial.mutationoperator

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import org.koin.java.KoinJavaComponent

class MutationOperatorWithSpreadSegment<S : ISpecimenRepresentation>(
    override val algorithm: BacterialAlgorithm<S>,
) : BacterialMutationOperator<S> {

    val statistics: BacterialAlgorithmStatistics by KoinJavaComponent.inject(BacterialAlgorithmStatistics::class.java)

    override fun invoke(
        clone: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ) {
        synchronized(statistics) {
            statistics.mutationImprovement = statistics.mutationImprovement.run {
                copy(operatorCallCount = operatorCallCount + 1)
            }
        }

        val shuffler = (0 until algorithm.cloneSegmentLength).shuffled()
        selectedPositions.forEachIndexed { index, position ->
            clone[position] = selectedElements[shuffler[index]]
        }
    }
}