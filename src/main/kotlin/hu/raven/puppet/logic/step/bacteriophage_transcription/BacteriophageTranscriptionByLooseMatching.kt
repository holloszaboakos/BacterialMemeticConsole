package hu.raven.puppet.logic.step.bacteriophage_transcription

import hu.akos.hollo.szabo.collections.slice
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesBigger
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.BacteriophageSpecimen
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.BacteriophageAlgorithmState
import hu.raven.puppet.model.utility.SimpleGraphEdge
import hu.raven.puppet.utility.buildPermutation
import java.io.File

class BacteriophageTranscriptionByLooseMatching<T>(
    override val infectionRate: Float,
    override val lifeReductionRate: Float,
    override val lifeCoefficient: Float,
    override val calculateCost: CalculateCost<T>
) : BacteriophageTranscription<T>() {
    override fun invoke(state: BacteriophageAlgorithmState<T>) {
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
                toRemove.sourceNodeIndex != it.sourceNodeIndex && toRemove.targetNodeIndex != it.targetNodeIndex
            }
        }

        val permutation = buildPermutation(specimen.permutation.size) {
            reducedEdges.forEach { edge -> addEdge(edge) }

            if (isComplete()) {
                return@buildPermutation
            }

            virus.addedEdges
                .asSequence()
                .filter { edge -> isAvailable(edge) }
                .forEach { edge -> addEdge(edge) }

            if (isComplete()) {
                return@buildPermutation
            }

            completeWithRandomAvailableEdges()

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