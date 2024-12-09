package hu.raven.puppet.logic.step.bacteriophage_transcription

import hu.akos.hollo.szabo.collections.slice
import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesBigger
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.SolutionWithIteration
import hu.raven.puppet.model.solution.partial.BacteriophageSpecimen
import hu.raven.puppet.model.state.BacteriophageAlgorithmState
import hu.raven.puppet.model.utility.math.CompleteGraph
import hu.raven.puppet.model.utility.math.GraphEdge
import hu.raven.puppet.utility.buildPermutation
import kotlin.random.Random

class BacteriophageTranscriptionByLooseMatchingAndHeuristicCompletion<E>(
    override val infectionRate: Float,
    override val lifeReductionRate: Float,
    override val lifeCoefficient: Float,
    override val calculateCost: CalculateCost<Permutation, *>,
    val costGraph: CompleteGraph<*, E>,
    val extractEdgeWeight: (E) -> Float,
) : BacteriophageTranscription<Permutation>() {
    override fun invoke(state: BacteriophageAlgorithmState<Permutation>) {
        if (state.virusPopulation.activesAsSequence().count() == 0) return
        state.virusPopulation.activesAsSequence()
            .onEach { virus ->
                if ((state.population.poolSize * infectionRate).toInt() == 0) return@onEach

                val fitness = state.population.activesAsSequence()
                    .shuffled()
                    .slice(0..<(state.population.poolSize * infectionRate).toInt())
                    .map { specimen ->
                        val oldCost = specimen.value.costOrException()
                        val oldPermutation = specimen.value.representation.clone()
                        applyVirus(specimen.value, virus.value)
                        val newCost = calculateCost(specimen.value.representation)
                        if (oldCost dominatesBigger newCost) {
                            oldPermutation.forEachIndexed { index, value ->
                                val oldIndex = specimen.value.representation.indexOf(value)
                                specimen.value.representation.swapValues(index, oldIndex)
                            }
                        } else {
                            if (!specimen.value.representation.indices.all { specimen.value.representation[it] == oldPermutation[it] }) {
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

    private fun applyVirus(specimen: SolutionWithIteration<Permutation>, virus: BacteriophageSpecimen) {
        val currentEdges = (0..specimen.representation.size)
            .map { GraphEdge<Unit>(specimen.representation.before(it), it, Unit) }
        val reducedEdges = currentEdges.filter {
            virus.removedEdges.all { toRemove ->
                toRemove.sourceNodeIndex != it.sourceNodeIndex && toRemove.targetNodeIndex != it.targetNodeIndex
            }
        }

        val representation = buildPermutation(specimen.representation.size) {
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

            val populationSize = specimen.representation.size
            val weightMatrix = Array(populationSize + 1) { sourceIndex ->
                FloatArray(populationSize + 1) { targetIndex ->
                    when {
                        sourceIndex == targetIndex -> 0f
                        else -> costGraph.edges[sourceIndex][targetIndex].let(extractEdgeWeight).toFloat()
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
                                isAvailable(GraphEdge(sourceIndex, targetIndex, Unit))
                            }
                            .sum()
                    }
                    .sum()

                if (weightSum == 0f) break

                var random = Random.nextFloat() * weightSum

                downWeighted.forEachIndexed outerForEach@{ sourceIndex, row ->
                    if (random <= 0f) return@outerForEach
                    row.forEachIndexed innerForEach@{ targetIndex, value ->
                        if (!isAvailable(GraphEdge(sourceIndex, targetIndex, Unit))) return@innerForEach

                        random -= value

                        if (random <= 0f) {
                            addEdge(GraphEdge(sourceIndex, targetIndex, Unit))
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
        specimen.representation.clear()
        representation
            .forEachIndexed { index, value ->
                specimen.representation[index] = value
            }
    }
}