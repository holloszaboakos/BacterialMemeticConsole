package hu.raven.puppet.logic.evolutionary.genetic

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.inject

class CrossOvers<S : ISpecimenRepresentation>(
    val algorithm: GeneticAlgorithm<S>
) {
    val crossoverOperator: CrossOverOperator by inject(CrossOverOperator::class.java)

    operator fun invoke() = runBlocking {
        val children = algorithm.population.filter { !it.inUse }
        val parent = algorithm.population.filter { it.inUse }.shuffled()
        parent.mapIndexed { index, primerParent ->
            val seconderParent =
                if (index % 2 == 0)
                    parent[index + 1]
                else
                    parent[index - 1]
            crossoverOperator(Pair(primerParent, seconderParent), children[index], algorithm)
        }

    }
}