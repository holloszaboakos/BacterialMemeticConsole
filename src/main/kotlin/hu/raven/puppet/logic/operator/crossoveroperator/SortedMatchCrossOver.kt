package hu.raven.puppet.logic.operator.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.task.CostGraph
import hu.raven.puppet.utility.extention.get
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.FloatSumExtensions.sumClever
import kotlin.math.abs

//finds longest sequence in parents with same ends and same values but in any order
//select cheaper order
//copy segment
//fill other values in primary order

class SortedMatchCrossOver(
    val costGraph: CostGraph
) : CrossOverOperator {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        childPermutation.clear()

        //size of longest matching slice found
        var longestSliceSize = 0
        var foundSlices = listOf<IntArray>()

        for (firstValue in 0 until parentPermutations.first.size - 1) {
            for (secondValue in firstValue until parentPermutations.first.size) {

                if (
                //values same distance away in both parents
                    parentPermutations[0].indexOf(firstValue) - parentPermutations[0].indexOf(secondValue)
                    ==
                    parentPermutations[1].indexOf(firstValue) - parentPermutations[1].indexOf(secondValue)
                    //distance is bigger than distance already found
                    && abs(parentPermutations[0].indexOf(firstValue) - parentPermutations[0].indexOf(secondValue)) > longestSliceSize
                ) {
                    val firstIndexes =
                        arrayOf(
                            parentPermutations[0].indexOf(firstValue),
                            parentPermutations[0].indexOf(secondValue)
                        ).sorted()
                    val secondIndexes =
                        arrayOf(
                            parentPermutations[1].indexOf(firstValue),
                            parentPermutations[1].indexOf(secondValue)
                        ).sorted()
                    val slices = listOf(
                        parentPermutations.first.slice(firstIndexes[0]..firstIndexes[1]),
                        parentPermutations.second.slice(secondIndexes[0]..secondIndexes[1])
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
            val cheaperIndex = Array(2) { sliceIndex ->
                (1 until foundSlices[sliceIndex].size)
                    .map { geneIndex ->
                        val previousValueOfSlice = foundSlices[sliceIndex][geneIndex - 1]
                        val currentValueOfSlice = foundSlices[sliceIndex][geneIndex]
                        costGraph
                            .getEdgeBetween(previousValueOfSlice, currentValueOfSlice)
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
            (indices[0].last + 1 until parentPermutations.first.size)
                .forEach { geneIndex ->
                    childPermutation[geneIndex] =
                        parentPermutations[0][geneIndex]
                }
        } else {
            childPermutation.indices.forEach { index ->
                childPermutation[index] = parentPermutations[0][index]
            }
        }
    }
}