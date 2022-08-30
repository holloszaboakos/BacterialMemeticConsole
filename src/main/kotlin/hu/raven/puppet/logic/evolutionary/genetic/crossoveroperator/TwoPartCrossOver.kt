package hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class TwoPartCrossOver : CrossOverOperator {
    override fun <S : ISpecimenRepresentation> invoke(parents: Pair<S, S>, child: S, algorithm: GeneticAlgorithm<S>) {
        TODO("Not yet implemented")
    }
}