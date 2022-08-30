package hu.raven.puppet.logic.evolutionary

import hu.raven.puppet.logic.evolutionary.setup.GeneticAlgorithmSetup
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import org.koin.java.KoinJavaComponent.inject

class GeneticAlgorithm<S : ISpecimenRepresentation>(
    iterationLimit: Int,
    sizeOfPopulation: Int
) : SEvolutionaryAlgorithm<S>(
    iterationLimit,
    sizeOfPopulation //4 * (costGraph.objectives.size + salesmen.size)
) {
    override val setup: GeneticAlgorithmSetup by inject(GeneticAlgorithmSetup::class.java)

    fun selection() = setup.selection(this)
    fun crossover() = setup.crossover(this)
    fun crossoverOperator(
        parents: Pair<S, S>,
        child: S
    ) = setup.crossoverOperator(parents, child, this)

    fun mutate() = setup.mutate(this)
}