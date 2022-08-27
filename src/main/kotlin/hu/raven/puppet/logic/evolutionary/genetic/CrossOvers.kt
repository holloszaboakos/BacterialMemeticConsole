package hu.raven.puppet.logic.evolutionary.genetic

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlinx.coroutines.runBlocking

class CrossOvers {
    operator fun <S : ISpecimenRepresentation> invoke(algorithm: GeneticAlgorithm<S>) = runBlocking {
        val children = algorithm.population.filter { !it.inUse }
        val parent = algorithm.population.filter { it.inUse }.shuffled()
        parent.mapIndexed { index, primerParent ->
            val seconderParent =
                if (index % 2 == 0)
                    parent[index + 1]
                else
                    parent[index - 1]
            algorithm.crossoverOperator(Pair(primerParent, seconderParent), children[index])
        }

    }
}