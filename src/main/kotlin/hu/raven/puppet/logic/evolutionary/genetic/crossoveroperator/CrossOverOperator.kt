package hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface CrossOverOperator<S : ISpecimenRepresentation> {
    val algorithm: GeneticAlgorithm<S>

    operator fun  invoke(
        parents: Pair<S, S>,
        child: S
    )
}