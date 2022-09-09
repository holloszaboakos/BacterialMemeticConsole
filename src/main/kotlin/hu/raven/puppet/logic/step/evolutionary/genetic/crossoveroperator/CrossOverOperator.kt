package hu.raven.puppet.logic.step.evolutionary.genetic.crossoveroperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.evolutionary.EvolutionaryAlgorithmStep

sealed class CrossOverOperator<S : ISpecimenRepresentation> : EvolutionaryAlgorithmStep<S>() {

    abstract operator fun invoke(
        parents: Pair<S, S>,
        child: S
    )
}