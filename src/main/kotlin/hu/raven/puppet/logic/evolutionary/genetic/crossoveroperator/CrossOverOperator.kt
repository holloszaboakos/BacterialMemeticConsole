package hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface CrossOverOperator {
    operator fun <S : ISpecimenRepresentation> invoke(
        parents: Pair<S, S>,
        child: S,
        algorithm: GeneticAlgorithm<S>
    )
}