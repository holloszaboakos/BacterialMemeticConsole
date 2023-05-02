package hu.raven.puppet.logic.operator.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.task.CostGraph
import hu.raven.puppet.utility.extention.get
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.sumClever
import kotlin.math.abs

//broken
class SortedMatchCrossOver(
    val costGraph: CostGraph
) : CrossOverOperator() {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        var longestSliceSize = 0
        var foundSlices = listOf<IntArray>()
        for (firstValue in 0 until parentPermutations.first.size - 1) {
            for (secondValue in firstValue until parentPermutations.first.size) {
                if (
                    parentPermutations[0].indexOf(firstValue) - parentPermutations[0].indexOf(secondValue)
                    ==
                    parentPermutations[1].indexOf(firstValue) - parentPermutations[1].indexOf(secondValue)
                    && abs(parentPermutations[0].indexOf(firstValue) - parentPermutations[0].indexOf(secondValue)) > longestSliceSize
                ) {
                    val firstIndices =
                        arrayOf(
                            parentPermutations[0].indexOf(firstValue),
                            parentPermutations[0].indexOf(secondValue)
                        ).sorted()
                    val secondIndices =
                        arrayOf(
                            parentPermutations[1].indexOf(firstValue),
                            parentPermutations[1].indexOf(secondValue)
                        ).sorted()
                    val slices = listOf(
                        parentPermutations.first.slice(firstIndices[0]..firstIndices[1]),
                        parentPermutations.second.slice(secondIndices[0]..secondIndices[1])
                    )
                    if (slices[0].all { slices[1].contains(it) }) {
                        longestSliceSize =
                            abs(parentPermutations[0].indexOf(firstValue) - parentPermutations[0].indexOf(secondValue))
                        foundSlices = slices.map { it.toList().toIntArray() }.toList()
                    }
                }
            }
        }
        if (foundSlices.isNotEmpty()) {
            val costGraph = costGraph
            val cheaperIndex = Array(2) { sliceIndex ->
                (1 until foundSlices[sliceIndex].size)
                    .map { geneIndex ->
                        costGraph
                            .getEdgeBetween(foundSlices[sliceIndex][geneIndex - 1], foundSlices[sliceIndex][geneIndex])
                            .length
                            .value
                    }
                    .sumClever()
            }.let { costs -> costs.indexOf(costs.min()) }
            val indices = Array(2) { index ->
                parentPermutations[index].indexOf(foundSlices[index].first())..
                        parentPermutations[index].indexOf(foundSlices[index].last())
            }
            (0 until indices[0].first).forEach { geneIndex ->
                childPermutation[geneIndex] =
                    parentPermutations[0][geneIndex]
            }
            indices[0].forEach { geneIndex ->
                childPermutation[geneIndex] =
                    foundSlices[cheaperIndex][geneIndex - indices[0].first]
            }
            (indices[0].last + 1 until parentPermutations.first.size).forEach { geneIndex ->
                childPermutation[geneIndex] =
                    parentPermutations.toList()[0][geneIndex]
            }
        }
    }
}