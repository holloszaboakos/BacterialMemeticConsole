package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class GeneticEdgeRecombinationCrossOver<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>,
) :
    CrossOverOperator<C>() {

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
    ) {
        val parentsL = parents.toList()
        val parentsInverses = Array(2) { parentsL[it].permutation.inverse() }

        child.permutation.setEach { _, _ -> child.permutation.size }
        val childContains = BooleanArray(child.permutation.size) { false }
        val randomPermutation = IntArray(child.permutation.size) { it }
        randomPermutation.shuffle()
        var lastIndex = 0

        //O(n2)
        val table = Array(parents.first.permutation.indices.count()) { valueIndex ->
            val neighbours = mutableSetOf<Int>()

            parentsL.forEachIndexed { parentIndex, parent ->
                if (parentsInverses[parentIndex][valueIndex] != 0)
                    neighbours += parent.permutation[parentsInverses[parentIndex][valueIndex] - 1]
                if (parentsInverses[parentIndex][valueIndex] != child.permutation.size - 1)
                    neighbours += parent.permutation[parentsInverses[parentIndex][valueIndex] + 1]
            }
            neighbours
        }

        val neighbourCounts = Array(child.permutation.size) { valueIndex ->
            table[valueIndex].size
        }

        child.permutation[0] = parents.first.permutation[0]
        childContains[child.permutation[0]] = true
        table[child.permutation[0]].forEach { neighbour ->
            table[neighbour].remove(child.permutation[0])
            neighbourCounts[neighbour]--
        }
        //O(n2)
        for (geneIndex in 1 until child.permutation.size) {
            val previousGene = child.permutation[geneIndex - 1]
            val neighborsOfPrevious = table[previousGene]
            if (neighborsOfPrevious.isNotEmpty()) {
                val neighbourCountsOfNeighbours = neighborsOfPrevious.map { neighbourCounts[it] }
                val minCount = neighbourCountsOfNeighbours.minOf { it }
                child.permutation[geneIndex] = neighborsOfPrevious
                    .filterIndexed { index, _ ->
                        neighbourCountsOfNeighbours[index] == minCount
                    }.random()
            } else {
                for (index in lastIndex until randomPermutation.size) {
                    if (!childContains[randomPermutation[index]]) {
                        child.permutation[geneIndex] = randomPermutation[index]
                        lastIndex = index + 1
                        break
                    }
                }
            }
            if (child.permutation[geneIndex] == child.permutation.size)
                println("FUCK")
            childContains[child.permutation[geneIndex]] = true
            table[child.permutation[geneIndex]].forEach { neighbour ->
                table[neighbour].remove(child.permutation[geneIndex])
                neighbourCounts[neighbour]--
            }
            neighborsOfPrevious.clear()
        }
        child.iteration = algorithmState.iteration
        child.cost = null
        child.inUse = true


        if (!child.permutation.checkFormat())
            throw Error("Invalid specimen!")
    }
}