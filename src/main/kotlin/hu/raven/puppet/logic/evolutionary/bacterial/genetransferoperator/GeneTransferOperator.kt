package hu.raven.puppet.logic.evolutionary.bacterial.genetransferoperator

import hu.raven.puppet.logic.common.logging.DoubleLogger
import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.extention.nextSegmentStartPosition
import org.koin.java.KoinJavaComponent
import kotlin.random.Random

sealed interface GeneTransferOperator {

    operator fun <S : ISpecimenRepresentation> invoke(
        algorithm: BacterialAlgorithm<S>,
        source: S,
        target: S
    )
}