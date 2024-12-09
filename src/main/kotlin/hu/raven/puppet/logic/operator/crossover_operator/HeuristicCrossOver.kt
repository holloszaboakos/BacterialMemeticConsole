package hu.raven.puppet.logic.operator.crossover_operator

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.calculus.multiplicativeInverse
import hu.akos.hollo.szabo.primitives.get
import hu.raven.puppet.logic.operator.weighted_selection.RouletteWheelSelection
import hu.raven.puppet.model.utility.math.CompleteGraph
import kotlin.math.min
import kotlin.random.Random.Default.nextInt

//random first value
//iterate:
// gather neighbour
//distance based random selection
class HeuristicCrossOver<T>(
    private val costGraph: CompleteGraph<*, T>,
    private val extractEdgeCost: (T) -> Float
) : CrossOverOperator<Permutation> {

    private val rouletteWheelSelection = RouletteWheelSelection<Int>()

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {

        val randomPermutation = IntArray(childPermutation.size) { it }
        randomPermutation.shuffle()
        var lastIndexUsed = 0

        childPermutation.clear()
        childPermutation[0] = nextInt(childPermutation.size)

        for (geneIndex in 1..<childPermutation.size) {

            val previousValue = childPermutation[geneIndex - 1]

            val neighbours = gatherNeighbouringValues(
                parentPermutations,
                previousValue,
                childPermutation
            )

            if (neighbours.isEmpty()) {
                lastIndexUsed = chooseNextValueAtRandom(
                    lastIndexUsed,
                    randomPermutation,
                    childPermutation,
                    geneIndex
                )
                continue
            }

            val weights = calculateWeightForNeighbours(
                neighbours,
                previousValue
            )

            chooseNextValueBasedOnWeight(
                weights,
                childPermutation,
                geneIndex,
                neighbours
            )
        }

    }

    private fun gatherNeighbouringValues(
        parentPermutations: Pair<Permutation, Permutation>,
        previousValue: Int,
        child: Permutation
    ): List<Int> {
        return listOf(
            parentPermutations[0][(parentPermutations[0].indexOf(previousValue) + child.size - 1) % child.size],
            parentPermutations[0][(parentPermutations[0].indexOf(previousValue) + 1) % child.size],
            parentPermutations[1][(parentPermutations[1].indexOf(previousValue) + child.size - 1) % child.size],
            parentPermutations[1][(parentPermutations[1].indexOf(previousValue) + 1) % child.size]
        ).filter { !child.contains(it) }
    }

    private fun chooseNextValueAtRandom(
        lastIndexUsed: Int,
        randomPermutation: IntArray,
        child: Permutation,
        geneIndex: Int
    ): Int {

        for (index in lastIndexUsed..<randomPermutation.size) {
            if (!child.contains(randomPermutation[index])) {
                child[geneIndex] = randomPermutation[index]
                return index + 1
            }
        }

        return randomPermutation.size - 1
    }

    private fun calculateWeightForNeighbours(
        neighbours: List<Int>,
        previousValue: Int
    ): Array<Float> =
        Array(neighbours.size) { neighbourIndex ->
            costGraph.edges[
                min(previousValue, costGraph.edges.size - 1)
            ][
                min(neighbours[neighbourIndex], costGraph.edges.size - 1)
            ]
                .let(extractEdgeCost)
                .multiplicativeInverse()
        }


    private fun chooseNextValueBasedOnWeight(
        weights: Array<Float>,
        child: Permutation,
        geneIndex: Int,
        neighbours: List<Int>
    ) {
        val candidatesWithWeight = neighbours
            .mapIndexed { index, value -> Pair(weights[index], value) }
            .toTypedArray()
        child[geneIndex] = rouletteWheelSelection(candidatesWithWeight)
    }
}