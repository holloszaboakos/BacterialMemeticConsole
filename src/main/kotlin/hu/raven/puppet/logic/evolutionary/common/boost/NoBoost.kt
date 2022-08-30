package hu.raven.puppet.logic.evolutionary.common.boost

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class NoBoost<S : ISpecimenRepresentation>(
    override val algorithm: SEvolutionaryAlgorithm<S>
) : Boost<S> {
    override suspend operator fun invoke() {
    }
}