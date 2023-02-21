package hu.raven.puppet.logic.step.evolutionary.genetic.crossoveroperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.physics.math.Fraction
import hu.raven.puppet.model.physics.sum
import hu.raven.puppet.model.task.graph.DEdge
import hu.raven.puppet.model.task.graph.DGraph
import kotlin.random.Random
import kotlin.random.Random.Default.nextInt

class HeuristicCrossOver<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : CrossOverOperator<S, C>() {

    override fun invoke(
        parents: Pair<S, S>,
        child: S,
    ) {
        val parentsL = parents.toList()
        val parentsInverse = Array(2) {
            parentsL[it].inverseOfPermutation()
        }

        val randomPermutation = IntArray(child.permutationSize) { it }
        randomPermutation.shuffle()
        var lastIndexUsed = 0

        val childContains = BooleanArray(child.permutationSize) { false }

        child.setEach { _, _ -> child.permutationSize }
        child[0] = nextInt(child.permutationSize)
        childContains[child[0]] = true

        for (geneIndex in 1 until child.permutationSize) {

            val previousValue = child[geneIndex - 1]

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

                if (child[geneIndex] == child.permutationSize)
                    logger("Failed to choose at random")

                continue
            }

            val weights = calculateWeightForNeighbours(
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

            if (child[geneIndex] == child.permutationSize)
                println("Failed to choose based on weights")


        }

        child.iteration = algorithmState.iteration
        child.costCalculated = false
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen after heuristic crossover!")

    }

    private fun <S : ISpecimenRepresentation<C>> gatherNeighbouringValues(
        parentsL: List<S>,
        parentsInverse: Array<IntArray>,
        previousValue: Int,
        child: S,
        childContains: BooleanArray
    ): List<Int> {
        return listOf(
            parentsL[0][(parentsInverse[0][previousValue] + child.permutationSize - 1) % child.permutationSize],
            parentsL[0][(parentsInverse[0][previousValue] + 1) % child.permutationSize],
            parentsL[1][(parentsInverse[1][previousValue] + child.permutationSize - 1) % child.permutationSize],
            parentsL[1][(parentsInverse[1][previousValue] + 1) % child.permutationSize]
        ).filter { !childContains[it] }
    }

    private fun <S : ISpecimenRepresentation<C>> chooseNextValueAtRandom(
        lastIndexUsed: Int,
        randomPermutation: IntArray,
        childContains: BooleanArray,
        child: S,
        geneIndex: Int
    ): Int {

        for (index in lastIndexUsed until randomPermutation.size) {
            if (!childContains[randomPermutation[index]]) {
                child[geneIndex] = randomPermutation[index]
                childContains[child[geneIndex]] = true
                return index + 1
            }
        }

        return randomPermutation.size - 1
    }

    private fun calculateWeightForNeighbours(
        neighbours: List<Int>,
        previousValue: Int
    ): Array<Meter> = taskHolder.task.costGraph.run {
        Array(neighbours.size) { neighbourIndex ->
            when {
                previousValue < objectives.size && neighbours[neighbourIndex] < objectives.size -> {
                    getEdgeBetween(previousValue, neighbours[neighbourIndex])
                        .length
                        .multiplicativeInverse()
                }

                previousValue < objectives.size -> {
                    edgesToCenter[previousValue]
                        .length
                        .multiplicativeInverse()
                }

                neighbours[neighbourIndex] < objectives.size -> {
                    edgesFromCenter[neighbours[neighbourIndex]]
                        .length
                        .multiplicativeInverse()
                }

                else -> Meter(1)
            }
        }
    }

    private fun DGraph.getEdgeBetween(from: Int, to: Int): DEdge {
        return edgesBetween[from]
            .values[if (to > from) to - 1 else to]
    }

    private fun Meter.multiplicativeInverse() = Meter(Fraction(value.value.second, value.value.first))

    private fun <S : ISpecimenRepresentation<C>> chooseNextValueBasedOnWeight(
        weights: Array<Meter>,
        child: S,
        geneIndex: Int,
        neighbours: List<Int>,
        childContains: BooleanArray
    ) {
        val sum = weights.sum()
        var choice = Random.nextDouble(sum.value.toDouble())

        for (weightIndex in weights.indices) {
            choice -= weights[weightIndex].value.toDouble()
            if (choice <= 0) {
                child[geneIndex] = neighbours[weightIndex]
                childContains[child[geneIndex]] = true
                break
            }
        }
    }
}