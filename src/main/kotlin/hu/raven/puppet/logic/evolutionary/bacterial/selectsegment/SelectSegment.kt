package hu.raven.puppet.logic.evolutionary.bacterial.selectsegment

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface SelectSegment<S : ISpecimenRepresentation> {

    val algorithm: BacterialAlgorithm<S>

    operator fun invoke(
        specimen: S
    ): IntArray
}