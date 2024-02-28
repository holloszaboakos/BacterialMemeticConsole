package hu.raven.puppet.logic.step.bacteriophage_transcription

import hu.akos.hollo.szabo.collections.slice
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesBigger
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.BacteriophageSpecimen
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.BacteriophageAlgorithmState
import hu.raven.puppet.model.utility.math.CompleteGraphWithCenterVertex
import hu.raven.puppet.model.utility.math.GraphEdge
import hu.raven.puppet.utility.buildPermutation
import kotlin.random.Random

class BacteriophageTranscriptionByLooseMatchingAndHeuristicCompletion<T>(
    override val infectionRate: Float,
    override val lifeReductionRate: Float,
    override val lifeCoefficient: Float,
    override val calculateCost: CalculateCost<T>,
    val task: CompleteGraphWithCenterVertex<Unit, Unit, Int>
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
            .map { GraphEdge<Unit>(specimen.permutation.before(it), it, Unit) }
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
                        sourceIndex == populationSize -> task.edgesFromCenter[targetIndex].value.toFloat()
                        targetIndex == populationSize -> task.edgesToCenter[sourceIndex].value.toFloat()
                        else -> task.edgesBetween[sourceIndex][targetIndex].value.toFloat()
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
                                isAvailable(GraphEdge<Unit>(sourceIndex, targetIndex, Unit))
                            }
                            .sum()
                    }
                    .sum()

                if (weightSum == 0f) break

                var random = Random.nextFloat() * weightSum

                downWeighted.forEachIndexed outerForEach@{ sourceIndex, row ->
                    if (random <= 0f) return@outerForEach
                    row.forEachIndexed innerForEach@{ targetIndex, value ->
                        if (!isAvailable(GraphEdge<Unit>(sourceIndex, targetIndex, Unit))) return@innerForEach

                        random -= value

                        if (random <= 0f) {
                            addEdge(GraphEdge<Unit>(sourceIndex, targetIndex, Unit))
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