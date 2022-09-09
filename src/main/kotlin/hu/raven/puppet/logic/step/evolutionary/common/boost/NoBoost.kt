package hu.raven.puppet.logic.step.evolutionary.common.boost

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class NoBoost<S : ISpecimenRepresentation> : Boost<S>() {
    override suspend operator fun invoke() {
    }
}