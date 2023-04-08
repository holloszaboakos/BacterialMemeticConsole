package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.task.CostGraph
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.sumClever
import kotlin.random.Random.Default.nextInt

class HeuristicCrossOver<C : PhysicsUnit<C>>(
    val costGraphProvider: () -> CostGraph
) : CrossOverOperator<C>() {

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
    ) {
        val costGraph = costGraphProvider()
        val parentsL = parents.toList()
        val parentsInverse = Array(2) {
            parentsL[it].permutation.inverse()
        }

        val randomPermutation = IntArray(child.permutation.size) { it }
        randomPermutation.shuffle()
        var lastIndexUsed = 0

        val childContains = BooleanArray(child.permutation.size) { false }

        child.permutation.setEach { _, _ -> child.permutation.size }
        child.permutation[0] = nextInt(child.permutation.size)
        childContains[child.permutation[0]] = true

        for (geneIndex in 1 until child.permutation.size) {

            val previousValue = child.permutation[geneIndex - 1]

            val neighbours = gatherNeighbouringValues(
                parentsL,
                parentsInverse,
                previousValue,
                child,
                childContains
            )

            if (neighbours.isEmpty()) {
                lastIndexUsed = chooseNextValueAtRandom(
                    lastIndexUsed,
                    randomPermutation,
                    childContains,
                    child,
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
                child,
                geneIndex,
                neighbours,
                childContains
            )

            if (child.permutation[geneIndex] == child.permutation.size)
                println("Failed to choose based on weights")


        }

    }

    private fun gatherNeighbouringValues(
        parentsL: List<OnePartRepresentation<C>>,
        parentsInverse: Array<Permutation>,
        previousValue: Int,
        child: OnePartRepresentation<C>,
        childContains: BooleanArray
    ): List<Int> {
        return listOf(
            parentsL[0].permutation[(parentsInverse[0][previousValue] + child.permutation.size - 1) % child.permutation.size],
            parentsL[0].permutation[(parentsInverse[0][previousValue] + 1) % child.permutation.size],
            parentsL[1].permutation[(parentsInverse[1][previousValue] + child.permutation.size - 1) % child.permutation.size],
            parentsL[1].permutation[(parentsInverse[1][previousValue] + 1) % child.permutation.size]
        ).filter { !childContains[it] }
    }

    private fun chooseNextValueAtRandom(
        lastIndexUsed: Int,
        randomPermutation: IntArray,
        childContains: BooleanArray,
        child: OnePartRepresentation<C>,
        geneIndex: Int
    ): Int {

        for (index in lastIndexUsed until randomPermutation.size) {
            if (!childContains[randomPermutation[index]]) {
                child.permutation[geneIndex] = randomPermutation[index]
                childContains[child.permutation[geneIndex]] = true
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
        child: OnePartRepresentation<C>,
        geneIndex: Int,
        neighbours: List<Int>,
        childContains: BooleanArray
    ) {
        val sum = weights.sumClever()
        //TODO stabilize
        var choice = Fraction.randomUntil(sum)

        for (weightIndex in weights.indices) {
            if (choice <= weights[weightIndex]) {
                child.permutation[geneIndex] = neighbours[weightIndex]
                childContains[child.permutation[geneIndex]] = true
                break
            }
            choice -= weights[weightIndex]
        }
    }
}