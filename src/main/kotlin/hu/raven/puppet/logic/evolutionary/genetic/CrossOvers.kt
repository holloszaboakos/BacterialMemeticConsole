package hu.raven.puppet.logic.evolutionary.genetic

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.inject

class CrossOvers<S : ISpecimenRepresentation>(
    val algorithm: GeneticAlgorithm<S>
) {
    val crossoverOperator: CrossOverOperator<S> by inject(CrossOverOperator::class.java)

    operator fun invoke() = runBlocking {
        val children = algorithm.population
            .filter { !it.inUse }
            .chunked(2)
        val parent = algorithm.population
            .filter { it.inUse }
            .shuffled()
            .chunked(2)

        parent.forEachIndexed { index, parentPair ->
            crossoverOperator(Pair(parentPair[0], parentPair[1]), children[index][0])
            crossoverOperator(Pair(parentPair[1], parentPair[0]), children[index][1])
        }

    }
}