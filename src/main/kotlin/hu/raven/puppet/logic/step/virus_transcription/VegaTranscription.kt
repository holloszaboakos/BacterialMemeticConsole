package hu.raven.puppet.logic.step.virus_transcription

import hu.akos.hollo.szabo.collections.slice
import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesSmaller
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.SolutionWithIteration
import hu.raven.puppet.model.solution.partial.VirusSpecimen
import hu.raven.puppet.model.state.VirusAlgorithmState
import hu.raven.puppet.model.task.AlgorithmTask
import kotlin.random.Random

class VegaTranscription<T : AlgorithmTask>(
    override val virusInfectionRate: Float,
    override val lifeReductionRate: Float,
    override val lifeCoefficient: Float,
    private val calculateCost: CalculateCost<Permutation, T>
) : Transcription<Permutation>() {
    override fun invoke(state: VirusAlgorithmState<Permutation>) {
        state.virusPopulation.activesAsSequence()
            .onEach { (_, virus) ->
                val fitness = state.population.activesAsSequence()
                    .shuffled()
                    .slice(0..<(state.population.poolSize * virusInfectionRate).toInt())
                    .map { (_, specimen) ->
                        val oldCost = specimen.costOrException()
                        val oldPermutation = specimen.representation.clone()
                        applyVirus(specimen, virus)
                        val newCost = calculateCost(specimen.representation)
                        if (newCost dominatesSmaller oldCost) {
                            oldPermutation.forEachIndexed { index, value ->
                                val oldIndex = specimen.representation.indexOf(value)
                                specimen.representation.swapValues(index, oldIndex)
                            }
                        } else {
                            specimen.iterationOfCreation = state.iteration
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
            .filter { (_, virus) ->
                virus.lifeForce?.all { it < 0 } ?: false
            }
            .forEach {
                state.virusPopulation.deactivate(it.index)
            }
    }

    private fun applyVirus(specimen: SolutionWithIteration<Permutation>, virus: VirusSpecimen) {
        val randomStartPosition = Random.nextInt(specimen.representation.size - virus.genes.size + 1)

        virus.genes.forEachIndexed { index, gene ->
            val oldIndex = specimen.representation.indexOf(gene)
            specimen.representation.swapValues(index + randomStartPosition, oldIndex)
        }
    }

}