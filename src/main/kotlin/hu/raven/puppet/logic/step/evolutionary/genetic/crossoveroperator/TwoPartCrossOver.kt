package hu.raven.puppet.logic.step.evolutionary.genetic.crossoveroperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class TwoPartCrossOver<S : ISpecimenRepresentation> : CrossOverOperator<S>() {

    override fun invoke(
        parents: Pair<S, S>,
        child: S,
    ) {
        TODO("Not yet implemented")
    }
}