package hu.raven.puppet.logic.evolutionary.bacterial.genetransfer

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface GeneTransfer<S : ISpecimenRepresentation> {
    val algorithm: BacterialAlgorithm<S>
    suspend operator fun invoke()
}
