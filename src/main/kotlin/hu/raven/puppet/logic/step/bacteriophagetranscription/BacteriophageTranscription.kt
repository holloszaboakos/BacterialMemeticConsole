package hu.raven.puppet.logic.step.bacteriophagetranscription

import hu.akos.hollo.szabo.collections.slice
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesBigger
import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.solution.BacteriophageSpecimen
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.BacteriophageAlgorithmState
import hu.raven.puppet.model.utility.SimpleGraphEdge
import hu.raven.puppet.utility.buildPermutation
import java.io.File

class BacteriophageTranscription(
    private val infectionRate: Float,
    private val lifeReductionRate: Float,
    private val lifeCoefficient: Float,
    private val calculateCost: CalculateCost
) : EvolutionaryAlgorithmStep<BacteriophageAlgorithmState> {
    override fun invoke(state: BacteriophageAlgorithmState) {
        state.virusPopulation.activesAsSequence()
            .onEach { virus ->
                val fitness = state.population.activesAsSequence()
                    .shuffled()
                    .slice(0..<(state.population.poolSize * infectionRate).toInt())
                    .map { specimen ->
                        val oldCost = specimen.costOrException()
                        val oldPermutation = specimen.permutation.clone()
                        applyVirus(specimen, virus)
                        val newCost = calculateCost(specimen)
                        if (oldCost dominatesBigger newCost) {
                            oldPermutation.forEachIndexed { index, value ->
                                val oldIndex = specimen.permutation.indexOf(value)
                                specimen.permutation.swapValues(index, oldIndex)
                            }
                        } else {
                            if (!specimen.permutation.indices.all { specimen.permutation[it] == oldPermutation[it] }) {
                                File("output.txt").appendText("SUCCESSFUL MUTATION\n")
                                specimen.iterationOfCreation = state.iteration
                            }
                            specimen.cost = newCost
                        }
                        FloatArray(oldCost.size) {
                            oldCost[it] - newCost[it]
                        }
                    }
                    .reduce { left, right ->
                        FloatArray(left.size) {
                            left[it] + right[it]
                        }
                    }

                fitness.indices.forEach {
                    fitness[it] *= lifeCoefficient
                }

                virus.lifeForce = virus.lifeForce
                    ?.let { lifeForce ->
                        lifeForce.indices.forEach {
                            lifeForce[it] = lifeReductionRate * lifeForce[it] + fitness[it]
                        }
                        lifeForce
                    } ?: fitness
            }
            .filter { virus ->
                virus.lifeForce?.all { it < 0 } ?: false
            }
            .forEach {
                state.virusPopulation.deactivate(it.id)
            }
    }

    private fun applyVirus(specimen: OnePartRepresentationWithCostAndIterationAndId, virus: BacteriophageSpecimen) {
        val currentEdges = (0..specimen.permutation.size)
            .map { SimpleGraphEdge(specimen.permutation.before(it), it) }
        val reducedEdges = currentEdges.filter {
            virus.removedEdges.all { toRemove ->
                toRemove.sourceNodeIndex != it.sourceNodeIndex || toRemove.targetNodeIndex != it.targetNodeIndex
            }
        }

        val availabilityMatrix = Array(specimen.permutation.size + 1) {
            BooleanArray(specimen.permutation.size + 1) { true }
        }
        availabilityMatrix.indices.forEach { availabilityMatrix[it][it] = false }

        val permutation = buildPermutation(specimen.permutation.size) {
            reducedEdges.forEach { edge ->
                val segment = addEdge(edge)
                availabilityMatrix[edge.targetNodeIndex][edge.sourceNodeIndex] = false
                availabilityMatrix[segment.targetNodeIndex][segment.sourceNodeIndex] = false
                availabilityMatrix.indices.forEach {
                    availabilityMatrix[it][edge.targetNodeIndex] = false
                    availabilityMatrix[edge.sourceNodeIndex][it] = false
                }
            }

            if (isComplete()) {
                return@buildPermutation
            }

            virus.addedEdges.forEach { edge ->
                if (!availabilityMatrix[edge.sourceNodeIndex][edge.targetNodeIndex])
                    return@forEach
                val segment = addEdge(edge)
                availabilityMatrix[edge.targetNodeIndex][edge.sourceNodeIndex] = false
                availabilityMatrix[segment.targetNodeIndex][segment.sourceNodeIndex] = false
                availabilityMatrix.indices.forEach {
                    availabilityMatrix[it][edge.targetNodeIndex] = false
                    availabilityMatrix[edge.sourceNodeIndex][it] = false
                }
            }

            if (isComplete()) {
                return@buildPermutation
            }

            availabilityMatrix
                .asSequence()
                .flatMapIndexed { rowIndex, row ->
                    row.asSequence()
                        .mapIndexed { columnIndex, available ->
                            Triple(
                                rowIndex,
                                columnIndex,
                                available
                            )
                        }
                }
                .filter { it.third }
                .map { SimpleGraphEdge(it.first, it.second) }
                .shuffled()
                .forEach { edge ->
                    if (!availabilityMatrix[edge.sourceNodeIndex][edge.targetNodeIndex])
                        return@forEach
                    val segment = addEdge(edge)
                    availabilityMatrix[edge.targetNodeIndex][edge.sourceNodeIndex] = false
                    availabilityMatrix[segment.targetNodeIndex][segment.sourceNodeIndex] = false
                    availabilityMatrix.indices.forEach {
                        availabilityMatrix[it][edge.targetNodeIndex] = false
                        availabilityMatrix[edge.sourceNodeIndex][it] = false
                    }
                }

            if (isComplete()) {
                return@buildPermutation
            }

            addLastEdge()
        }
        specimen.permutation.clear()
        permutation
            .forEachIndexed { index, value ->
                specimen.permutation[index] = value
            }
    }
}