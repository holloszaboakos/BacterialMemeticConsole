package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation

class GeneticEdgeRecombinationCrossOver<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    CrossOverOperator<S, C>() {

    override fun invoke(
        parents: Pair<S, S>,
        child: S,
    ) {
        val parentsL = parents.toList()
        val parentsInverses = Array(2) { parentsL[it].inverseOfPermutation() }

        child.setEach { _, _ -> child.permutationSize }
        val childContains = BooleanArray(child.permutationSize) { false }
        val randomPermutation = IntArray(child.permutationSize) { it }
        randomPermutation.shuffle()
        var lastIndex = 0

        //O(n2)
        val table = Array(parents.first.permutationIndices.count()) { valueIndex ->
            val neighbours = mutableSetOf<Int>()

            parentsL.forEachIndexed { parentIndex, parent ->
                if (parentsInverses[parentIndex][valueIndex] != 0)
                    neighbours += parent[parentsInverses[parentIndex][valueIndex] - 1]
                if (parentsInverses[parentIndex][valueIndex] != child.permutationSize - 1)
                    neighbours += parent[parentsInverses[parentIndex][valueIndex] + 1]
            }
            neighbours
        }

        val neighbourCounts = Array(child.permutationSize) { valueIndex ->
            table[valueIndex].size
        }

        child[0] = parents.first[0]
        childContains[child[0]] = true
        table[child[0]].forEach { neighbour ->
            table[neighbour].remove(child[0])
            neighbourCounts[neighbour]--
        }
        //O(n2)
        for (geneIndex in 1 until child.permutationSize) {
            val previousGene = child[geneIndex - 1]
            val neighborsOfPrevious = table[previousGene]
            if (neighborsOfPrevious.isNotEmpty()) {
                val neighbourCountsOfNeighbours = neighborsOfPrevious.map { neighbourCounts[it] }
                val minCount = neighbourCountsOfNeighbours.minOf { it }
                child[geneIndex] = neighborsOfPrevious
                    .filterIndexed { index, _ ->
                        neighbourCountsOfNeighbours[index] == minCount
                    }.random()
            } else {
                for (index in lastIndex until randomPermutation.size) {
                    if (!childContains[randomPermutation[index]]) {
                        child[geneIndex] = randomPermutation[index]
                        lastIndex = index + 1
                        break
                    }
                }
            }
            if (child[geneIndex] == child.permutationSize)
                println("FUCK")
            childContains[child[geneIndex]] = true
            table[child[geneIndex]].forEach { neighbour ->
                table[neighbour].remove(child[geneIndex])
                neighbourCounts[neighbour]--
            }
            neighborsOfPrevious.clear()
        }
        child.iteration = algorithmState.iteration
        child.cost = null
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen!")
    }
}