package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.sumClever
import kotlin.random.Random.Default.nextInt

class HeuristicCrossOver<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val solutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<S, C>,
    val logger: DoubleLogger,
) : CrossOverOperator<S, C>() {

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
        child.cost = null
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen after heuristic crossover!")

    }

    private fun <S : SolutionRepresentation<C>> gatherNeighbouringValues(
        parentsL: List<S>,
        parentsInverse: Array<Permutation>,
        previousValue: Int,
        child: S,
        childContains: BooleanArray
    ): List<Int> {
        return listOf(
            parentsL[0][(parentsInverse[0].value[previousValue] + child.permutationSize - 1) % child.permutationSize],
            parentsL[0][(parentsInverse[0].value[previousValue] + 1) % child.permutationSize],
            parentsL[1][(parentsInverse[1].value[previousValue] + child.permutationSize - 1) % child.permutationSize],
            parentsL[1][(parentsInverse[1].value[previousValue] + 1) % child.permutationSize]
        ).filter { !childContains[it] }
    }

    private fun <S : SolutionRepresentation<C>> chooseNextValueAtRandom(
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
    ): Array<Fraction> = algorithmState.task.costGraph.run {
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


    private fun <S : SolutionRepresentation<C>> chooseNextValueBasedOnWeight(
        weights: Array<Fraction>,
        child: S,
        geneIndex: Int,
        neighbours: List<Int>,
        childContains: BooleanArray
    ) {
        val sum = weights.sumClever()
        //TODO stabilize
        var choice = Fraction.randomUntil(sum)

        for (weightIndex in weights.indices) {
            if (choice <= weights[weightIndex]) {
                child[geneIndex] = neighbours[weightIndex]
                childContains[child[geneIndex]] = true
                break
            }
            choice -= weights[weightIndex]
        }
    }
}