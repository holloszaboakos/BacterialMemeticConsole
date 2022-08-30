package hu.raven.puppet.logic.evolutionary

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class GeneticAlgorithm<S : ISpecimenRepresentation>(
    iterationLimit: Int,
    sizeOfPopulation: Int
) : SEvolutionaryAlgorithm<S>(
    iterationLimit,
    sizeOfPopulation //4 * (costGraph.objectives.size + salesmen.size)
)