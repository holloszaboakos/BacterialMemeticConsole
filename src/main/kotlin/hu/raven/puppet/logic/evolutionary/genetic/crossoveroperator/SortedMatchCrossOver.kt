package hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlin.math.abs

//broken
class SortedMatchCrossOver<S : ISpecimenRepresentation>(
    override val algorithm: GeneticAlgorithm<S>
) : CrossOverOperator<S> {

    override fun invoke(
        parents: Pair<S, S>,
        child: S,
    ) {
        val parentsInverse = listOf(
            Array(parents.first.permutationIndices.count()) {
                parents.first.indexOf(it)
            },
            Array(parents.second.permutationIndices.count()) {
                parents.second.indexOf(it)
            }
        )
        var longestSliceSize = 0
        var foundSlices = listOf<IntArray>()
        for (firstValue in 0 until parents.first.permutationIndices.count() - 1) {
            for (secondValue in firstValue until parents.first.permutationIndices.count()) {
                if (
                    parentsInverse[0][firstValue] - parentsInverse[0][secondValue]
                    ==
                    parentsInverse[1][firstValue] - parentsInverse[1][secondValue]
                    && abs(parentsInverse[0][firstValue] - parentsInverse[0][secondValue]) > longestSliceSize
                ) {
                    val firstIndices =
                        arrayOf(parentsInverse[0][firstValue], parentsInverse[0][secondValue]).sorted()
                    val secondIndices =
                        arrayOf(parentsInverse[1][firstValue], parentsInverse[1][secondValue]).sorted()
                    val slices = listOf(
                        parents.first.slice(firstIndices[0]..firstIndices[1]),
                        parents.second.slice(secondIndices[0]..secondIndices[1])
                    )
                    if (slices[0].all { slices[1].contains(it) }) {
                        longestSliceSize = abs(parentsInverse[0][firstValue] - parentsInverse[0][secondValue])
                        foundSlices = slices.map { it.toList().toIntArray() }.toList()
                    }
                }
            }
        }
        if (foundSlices.isNotEmpty()) {
            val cheaperIndex = LongArray(2) { sliceIndex ->
                (1 until foundSlices[sliceIndex].size).sumOf { geneIndex ->
                    algorithm.task.costGraph
                        .edgesBetween[foundSlices[sliceIndex][geneIndex - 1]]
                        .values[
                            if (foundSlices[sliceIndex][geneIndex] > foundSlices[sliceIndex][geneIndex - 1])
                                foundSlices[sliceIndex][geneIndex] - 1
                            else
                                foundSlices[sliceIndex][geneIndex]
                    ]
                        .length_Meter
                }
            }.let { costs -> costs.indexOf(costs.minOf { it }) }
            val indices = Array(2) { index ->
                parentsInverse[index][foundSlices[index].first()]..
                        parentsInverse[index][foundSlices[index].last()]
            }
            (0 until indices[0].first).forEach { geneIndex ->
                child[geneIndex] = parents.toList()[0][geneIndex]
            }
            indices[0].forEach { geneIndex ->
                child[geneIndex] = foundSlices[cheaperIndex][geneIndex - indices[0].first]
            }
            (indices[0].last + 1 until parents.first.permutationIndices.count()).forEach { geneIndex ->
                child[geneIndex] = parents.toList()[0][geneIndex]
            }
        }
        child.iteration = algorithm.iteration
        child.costCalculated = false
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen!")

    }
}