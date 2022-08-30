package hu.raven.puppet.logic.evolutionary.bacterial.genetransferoperator

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface GeneTransferOperator {

    operator fun <S : ISpecimenRepresentation> invoke(
        algorithm: BacterialAlgorithm<S>,
        source: S,
        target: S
    )
}