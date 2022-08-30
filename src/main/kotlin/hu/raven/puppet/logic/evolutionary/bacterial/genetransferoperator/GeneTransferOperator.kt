package hu.raven.puppet.logic.evolutionary.bacterial.genetransferoperator

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface GeneTransferOperator<S : ISpecimenRepresentation> {

    val algorithm: BacterialAlgorithm<S>

    operator fun invoke(
        source: S,
        target: S
    )
}