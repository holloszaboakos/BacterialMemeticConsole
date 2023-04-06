package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.min
import hu.raven.puppet.utility.extention.sumClever
import kotlin.math.abs

//broken
class SortedMatchCrossOver<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>
) : CrossOverOperator<C>() {

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
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
            val cheaperIndex = Array(2) { sliceIndex ->
                (1 until foundSlices[sliceIndex].size)
                    .map { geneIndex ->
                        algorithmState.task.costGraph
                            .getEdgeBetween(foundSlices[sliceIndex][geneIndex - 1], foundSlices[sliceIndex][geneIndex])
                            .length
                            .value
                    }
                    .sumClever()
            }.let { costs -> costs.indexOf(costs.min()) }
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
        child.iteration = algorithmState.iteration
        child.cost = null
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen!")

    }
}