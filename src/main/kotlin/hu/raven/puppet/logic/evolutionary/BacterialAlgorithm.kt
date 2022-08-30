package hu.raven.puppet.logic.evolutionary

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class BacterialAlgorithm<S : ISpecimenRepresentation>(
    iterationLimit: Int,
    sizeOfPopulation: Int,
    val cloneCount: Int = 10,
    val cloneSegmentLength: Int = 50,
    val cloneCycleCount: Int = 10, //gene count / clone segment length
    val geneTransferSegmentLength: Int = 300,
    val injectionCount: Int = 30
) : SEvolutionaryAlgorithm<S>(
    iterationLimit,
    sizeOfPopulation
) {
    val geneCount
        get() = population.first().permutationIndices.count()
}