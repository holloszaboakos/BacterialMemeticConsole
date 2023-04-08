package hu.raven.puppet.logic.step.initializePopulation

import hu.raven.puppet.logic.step.bacterialmutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.utility.extention.asPermutation
import kotlinx.coroutines.runBlocking

class InitializeHugePopulationThanPreOptimizeThanSelectBest<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>,
    private val mutationOperator: MutationOnSpecimen<C>,
    private val statistics: BacterialAlgorithmStatistics,
) : InitializePopulation<C>() {


    override fun invoke() {
        algorithmState.run {
            val sizeOfPermutation = task.costGraph.objectives.size + task.transportUnits.size - 1
            val basePermutation = IntArray(sizeOfPermutation) { it }

            population = createPopulation()

            population.forEachIndexed { instanceIndex, instance ->
                val step = instanceIndex % (sizeOfPermutation - 1) + 1
                if (step == 1) {
                    basePermutation.shuffle()
                }

                val newContains = BooleanArray(sizeOfPermutation) { false }
                val newPermutation = IntArray(sizeOfPermutation) { -1 }
                var baseIndex = step
                for (newIndex in 0 until sizeOfPermutation) {
                    if (newContains[basePermutation[baseIndex]])
                        baseIndex = (baseIndex + 1) % sizeOfPermutation
                    newPermutation[newIndex] = basePermutation[baseIndex]
                    newContains[basePermutation[baseIndex]] = true
                    baseIndex = (baseIndex + step) % sizeOfPermutation
                }
                instance.permutation.setEach { index, _ ->
                    newPermutation[index]
                }
                instance.iteration = 0
                instance.inUse = true
                instance.cost = null
            }

            val bestImprovements =
                runBlocking {
                    bacterialMutateEach()
                }

            population =
                bestImprovements
                    .map { it.first }
                    .slice(0 until parameters.sizeOfPopulation)
                    .mapIndexed { index, s ->
                        OnePartRepresentation<C>(
                            id = index,
                            permutation = s.permutation.clone(),
                            objectiveCount = s.objectiveCount,
                            inUse = true,
                            cost = null,
                            orderInPopulation = index,
                            iteration = 0
                        )
                    }
                    .toMutableList()
        }
    }

    private fun bacterialMutateEach() = algorithmState.population
        .map { specimen ->
            Pair(
                specimen.copy(),
                specimen.apply { mutationOperator(specimen, 0) }
            )
        }
        .sortedBy {
            it.first.costOrException().value / it.second.costOrException().value
        }


    private fun createPopulation(): MutableList<OnePartRepresentation<C>> {
        return if (algorithmState.task.costGraph.objectives.size != 1)
            ArrayList(List((algorithmState.task.costGraph.objectives.size + algorithmState.task.transportUnits.size - 1)) { specimenIndex ->
                OnePartRepresentation<C>(
                    id = specimenIndex,
                    objectiveCount = algorithmState.task.costGraph.objectives.size,
                    permutation = Permutation(IntArray(
                        algorithmState.task.transportUnits.size +
                                algorithmState.task.costGraph.objectives.size
                    ) { index ->
                        index
                    }),
                    inUse = true,
                    cost = null,
                    orderInPopulation = specimenIndex,
                    iteration = 0
                )
            })
        else
            arrayListOf(
                OnePartRepresentation<C>(
                    0,
                    1,
                    intArrayOf(0).asPermutation(),
                    inUse = true,
                    cost = null,
                    orderInPopulation = 0,
                    iteration = 0
                )
            )
    }
}