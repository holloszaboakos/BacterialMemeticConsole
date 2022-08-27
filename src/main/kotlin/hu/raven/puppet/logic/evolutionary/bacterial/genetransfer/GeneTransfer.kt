package hu.raven.puppet.logic.evolutionary.bacterial.genetransfer

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface GeneTransfer {
    suspend operator fun <S : ISpecimenRepresentation> invoke(algorithm: BacterialAlgorithm<S>)
}
