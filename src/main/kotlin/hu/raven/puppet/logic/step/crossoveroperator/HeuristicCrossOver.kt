package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.task.CostGraph
import hu.raven.puppet.utility.extention.get
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.sumClever
import kotlin.random.Random.Default.nextInt

class HeuristicCrossOver<C : PhysicsUnit<C>>(
    val costGraphProvider: () -> CostGraph
) : CrossOverOperator<C>() {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        val costGraph = costGraphProvider()

        val randomPermutation = IntArray(childPermutation.size) { it }
        randomPermutation.shuffle()
        var lastIndexUsed = 0

        childPermutation.setEach { _, _ -> childPermutation.size }
        childPermutation[0] = nextInt(childPermutation.size)

        for (geneIndex in 1 until childPermutation.size) {

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
                costGraph,
                neighbours,
                previousValue
            )

            chooseNextValueBasedOnWeight(
                weights,
                childPermutation,
                geneIndex,
                neighbours
            )

            if (childPermutation[geneIndex] == childPermutation.size)
                println("Failed to choose based on weights")


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

        for (index in lastIndexUsed until randomPermutation.size) {
            if (!child.contains(randomPermutation[index])) {
                child[geneIndex] = randomPermutation[index]
                return index + 1
            }
        }

        return randomPermutation.size - 1
    }

    private fun calculateWeightForNeighbours(
        costGraph: CostGraph,
        neighbours: List<Int>,
        previousValue: Int
    ): Array<Fraction> = costGraph.run {
        Array(neighbours.size) { neighbourIndex ->
            when {
                previousValue < objectives.size && neighbours[neighbourIndex] < objectives.size -> {
                    getEdgeBetween(previousValue, neighbours[neighbourIndex])
                        .length
                        .value
                        .multiplicativeInverse()
                }

                previousValue < objectives.size -> {
                    edgesToCenter[previousValue]
                        .length
                        .value
                        .multiplicativeInverse()
                }

                neighbours[neighbourIndex] < objectives.size -> {
                    edgesFromCenter[neighbours[neighbourIndex]]
                        .length
                        .value
                        .multiplicativeInverse()
                }

                else -> Fraction.new(1)
            }
        }
    }


    private fun chooseNextValueBasedOnWeight(
        weights: Array<Fraction>,
        child: Permutation,
        geneIndex: Int,
        neighbours: List<Int>
    ) {
        val sum = weights.sumClever()
        //TODO stabilize
        var choice = Fraction.randomUntil(sum)

        for (weightIndex in weights.indices) {
            if (choice <= weights[weightIndex]) {
                child[geneIndex] = neighbours[weightIndex]
                break
            }
            choice -= weights[weightIndex]
        }
    }
}