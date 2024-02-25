package hu.raven.puppet.logic.step.bacteriophage_transcription

import hu.akos.hollo.szabo.collections.slice
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesBigger
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.BacteriophageSpecimen
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.BacteriophageAlgorithmState
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.model.utility.SimpleGraphEdge
import hu.raven.puppet.utility.buildPermutation
import hu.raven.puppet.utility.extention.getEdgeBetween
import java.io.File
import kotlin.random.Random

class BacteriophageTranscriptionByLooseMatchingAndHeuristicCompletion(
    override val infectionRate: Float,
    override val lifeReductionRate: Float,
    override val lifeCoefficient: Float,
    override val calculateCost: CalculateCost,
    val task: Task
) : BacteriophageTranscription() {
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

            val populationSize = specimen.permutation.size
            val weightMatrix = Array(populationSize + 1) { sourceIndex ->
                FloatArray(populationSize + 1) { targetIndex ->
                    when {
                        sourceIndex == targetIndex -> 0f
                        sourceIndex == populationSize -> task.costGraph.edgesFromCenter[targetIndex].length.value
                        targetIndex == populationSize -> task.costGraph.edgesToCenter[sourceIndex].length.value
                        else -> task.costGraph.getEdgeBetween(sourceIndex, targetIndex).length.value
                    }
                }
            }
            val sumSourceWeights = weightMatrix.map { it.sum() }
            val sumTargetWeights = weightMatrix.indices.map { targetIndex ->
                weightMatrix.map { it[targetIndex] }.sum()
            }
            val downWeighted = Array(populationSize + 1) { sourceIndex ->
                FloatArray(populationSize + 1) { targetIndex ->
                    val downWeight =
                        sumSourceWeights[sourceIndex] +
                                sumTargetWeights[targetIndex] -
                                2 * weightMatrix[sourceIndex][targetIndex] +
                                weightMatrix[targetIndex][sourceIndex]
                    weightMatrix[sourceIndex][targetIndex] / downWeight
                }
            }

            while (true) {
                val weightSum = downWeighted
                    .mapIndexed { sourceIndex, row ->
                        row
                            .filterIndexed { targetIndex, _ ->
                                isAvailable(SimpleGraphEdge(sourceIndex, targetIndex))
                            }
                            .sum()
                    }
                    .sum()

                if (weightSum == 0f) break

                var random = Random.nextFloat() * weightSum

                downWeighted.forEachIndexed outerForEach@{ sourceIndex, row ->
                    if (random <= 0f) return@outerForEach
                    row.forEachIndexed innerForEach@{ targetIndex, value ->
                        if (!isAvailable(SimpleGraphEdge(sourceIndex, targetIndex))) return@innerForEach

                        random -= value

                        if (random <= 0f) {
                            addEdge(SimpleGraphEdge(sourceIndex, targetIndex))
                        }
                    }
                }
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