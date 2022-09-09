package hu.raven.puppet.logic.step.evolutionary.genetic

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.evolutionary.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.evolutionary.genetic.crossoveroperator.CrossOverOperator
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.runBlocking


class CrossOvers<S : ISpecimenRepresentation> : EvolutionaryAlgorithmStep<S>() {
    val crossoverOperator: CrossOverOperator<S> by inject()

    operator fun invoke() = runBlocking {
        val children = algorithmState.population
            .filter { !it.inUse }
            .chunked(2)
        val parent = algorithmState.population
            .filter { it.inUse }
            .shuffled()
            .chunked(2)

        parent.forEachIndexed { index, parentPair ->
            crossoverOperator(Pair(parentPair[0], parentPair[1]), children[index][0])
            crossoverOperator(Pair(parentPair[1], parentPair[0]), children[index][1])
        }

    }
}