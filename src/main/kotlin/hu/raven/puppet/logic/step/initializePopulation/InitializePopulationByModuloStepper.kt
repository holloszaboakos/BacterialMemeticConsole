package hu.raven.puppet.logic.step.initializePopulation

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem
import hu.raven.puppet.model.solution.PoolWithSmartActivation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.toPermutation

class InitializePopulationByModuloStepper<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>,
) :
    InitializePopulation<C>() {

    override fun invoke() {
        algorithmState.run {
            val sizeOfPermutation =
                (task.costGraph.objectives.size + task.transportUnits.size - 1)
            val basePermutation = IntArray(sizeOfPermutation) { it }
            population = if (task.costGraph.objectives.size != 1)
                MutableList((task.costGraph.objectives.size + task.transportUnits.size - 1)) {
                    OnePartRepresentationWithIteration<C>(
                        iterationOfCreation = 0,
                        cost = null,
                        objectiveCount = task.costGraph.objectives.size,
                        permutation = IntArray(
                            task.transportUnits.size +
                                    task.costGraph.objectives.size
                        ) { index ->
                            index
                        }.toPermutation()
                    )
                }.let { PoolWithSmartActivation(it) }
            else
                mutableListOf(
                    OnePartRepresentationWithIteration<C>(
                        0,
                        null,
                        1,
                        intArrayOf(0).toPermutation()
                    )
                ).let { PoolWithSmartActivation(it) }

            population.activateAll()
            population.mapActives { it }.forEachIndexed { instanceIndex, instance ->
                initSpecimen(
                    instanceIndex,
                    instance,
                    sizeOfPermutation,
                    basePermutation
                )
            }
        }
    }

    private fun initSpecimen(
        instanceIndex: Int,
        instance: PoolItem<OnePartRepresentationWithIteration<C>>,
        sizeOfPermutation: Int,
        basePermutation: IntArray
    ) {
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

        val breakPoints = newPermutation
            .mapIndexed { index, value ->
                if (value < algorithmState.task.costGraph.objectives.size)
                    -1
                else
                    index
            }
            .filter { it != -1 }
            .toMutableList()

        breakPoints.add(0, -1)
        breakPoints.add(sizeOfPermutation)
        instance.content.permutation.setEach { index, _ ->
            newPermutation[index]
        }
        instance.content.cost = null
    }
}