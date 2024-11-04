package hu.raven.puppet.logic.step.bacteriophage_transcription

import hu.akos.hollo.szabo.collections.slice
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesBigger
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.BacteriophageSpecimen
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.BacteriophageAlgorithmState
import hu.raven.puppet.model.utility.math.GraphEdge
import hu.raven.puppet.utility.buildPermutation

class BacteriophageTranscriptionByStrictMatching<T>(
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
                        val oldCost = specimen.value.costOrException()
                        val oldPermutation = specimen.value.permutation.clone()
                        applyVirus(specimen.value, virus.value)
                        val newCost = calculateCost(specimen.value)
                        if (oldCost dominatesBigger newCost) {
                            oldPermutation.forEachIndexed { index, value ->
                                val oldIndex = specimen.value.permutation.indexOf(value)
                                specimen.value.permutation.swapValues(index, oldIndex)
                            }
                        } else {
                            if (!specimen.value.permutation.indices.all { specimen.value.permutation[it] == oldPermutation[it] }) {
                                specimen.value.iterationOfCreation = state.iteration
                            }
                            specimen.value.cost = newCost
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

                virus.value.lifeForce = virus.value.lifeForce
                    ?.let { lifeForce ->
                        lifeForce.indices.forEach {
                            lifeForce[it] = lifeReductionRate * lifeForce[it] + fitness[it]
                        }
                        lifeForce
                    } ?: fitness
            }
            .filter { virus ->
                virus.value.lifeForce?.all { it < 0 } ?: false
            }
            .forEach {
                state.virusPopulation.deactivate(it.index)
            }
    }

    private fun applyVirus(specimen: OnePartRepresentationWithCostAndIteration, virus: BacteriophageSpecimen) {
        val currentEdges = (0..specimen.permutation.size)
            .map { GraphEdge<Unit>(specimen.permutation.before(it), it, Unit) }
        val reducedEdges = currentEdges.filter {
            virus.removedEdges.all { toRemove ->
                toRemove.sourceNodeIndex != it.sourceNodeIndex || toRemove.targetNodeIndex != it.targetNodeIndex
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