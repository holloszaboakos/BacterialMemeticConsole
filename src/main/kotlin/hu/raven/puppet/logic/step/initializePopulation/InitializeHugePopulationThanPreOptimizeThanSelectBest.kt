package hu.raven.puppet.logic.step.initializePopulation

import hu.raven.puppet.logic.step.bacterialmutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.solution.PoolWithSmartActivation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.slice
import hu.raven.puppet.utility.extention.toPermutation

class InitializeHugePopulationThanPreOptimizeThanSelectBest<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>,
    private val mutationOperator: MutationOnSpecimen<C>,
) : InitializePopulation<C>() {


    override fun invoke() {
        algorithmState.run {
            val sizeOfPermutation = task.costGraph.objectives.size + task.transportUnits.size - 1
            val basePermutation = IntArray(sizeOfPermutation) { it }

            population = createPopulation()

            population.activesAsSequence().forEachIndexed { instanceIndex, instance ->
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
                newPermutation.forEachIndexed { index, value ->
                    instance.permutation[index] = value
                }
                instance.iterationOfCreation = 0
                instance.cost = null
            }

            val bestImprovements = bacterialMutateEach()

            population =
                bestImprovements
                    .map { it.first }
                    .slice(0 until parameters.sizeOfPopulation)
                    .map { specimen ->
                        OnePartRepresentationWithCostAndIterationAndId(
                            id = specimen.value.id,
                            iterationOfCreation = specimen.value.iterationOfCreation,
                            cost = specimen.value.cost,
                            objectiveCount = specimen.value.objectiveCount,
                            permutation = specimen.value.permutation.clone()
                        )
                    }
                    .toMutableList()
                    .let { PoolWithSmartActivation(it) }
        }
    }

    private fun bacterialMutateEach() = algorithmState.population
        .activesAsSequence()
        .withIndex()
        .map { specimenWithIndex ->
            Pair(
                specimenWithIndex.copy(),
                specimenWithIndex.apply { mutationOperator(specimenWithIndex, 0) }
            )
        }
        .sortedBy {
            it.first.value.costOrException().value / it.second.value.costOrException().value
        }


    private fun createPopulation(): PoolWithSmartActivation<OnePartRepresentationWithCostAndIterationAndId<C>> {
        return if (algorithmState.task.costGraph.objectives.size != 1)
            MutableList((algorithmState.task.costGraph.objectives.size + algorithmState.task.transportUnits.size - 1)) {
                OnePartRepresentationWithCostAndIterationAndId<C>(
                    id = it,
                    iterationOfCreation = 0,
                    cost = null,
                    objectiveCount = algorithmState.task.costGraph.objectives.size,
                    permutation = IntArray(
                        algorithmState.task.transportUnits.size +
                                algorithmState.task.costGraph.objectives.size
                    ) { index -> index }
                        .toPermutation()
                )
            }.let { PoolWithSmartActivation(it) }
        else
            mutableListOf(
                OnePartRepresentationWithCostAndIterationAndId<C>(
                    id = 0,
                    iterationOfCreation = 0,
                    cost = null,
                    1,
                    intArrayOf(0).toPermutation()
                )
            ).let { PoolWithSmartActivation(it) }
    }
}