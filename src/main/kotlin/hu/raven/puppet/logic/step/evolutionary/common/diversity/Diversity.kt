package hu.raven.puppet.logic.step.evolutionary.common.diversity

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.logic.step.evolutionary.EvolutionaryAlgorithmStep
import hu.raven.puppet.utility.inject

sealed class Diversity<S : ISpecimenRepresentation> : EvolutionaryAlgorithmStep<S>() {
    val statistics: BacterialAlgorithmStatistics by inject()

    abstract operator fun invoke()
}