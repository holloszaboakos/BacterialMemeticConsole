package hu.raven.puppet.logic.evolutionary.bacterial.mutationoperator

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.Statistics
import hu.raven.puppet.utility.extention.shuffled
import org.koin.java.KoinJavaComponent.inject

class MutationOperatorWithContinuesSegment<S : ISpecimenRepresentation>(
    override val algorithm: BacterialAlgorithm<S>,

    ) : BacterialMutationOperator<S> {

    val statistics: Statistics by inject(Statistics::class.java)
    override fun invoke(
        clone: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ) {
        synchronized(statistics) {
            statistics.mutationOperatorCall++
        }

        selectedPositions
            .shuffled()
            .forEachIndexed { readIndex, writeIndex ->
                clone[writeIndex] = selectedElements[readIndex]
            }
    }
}