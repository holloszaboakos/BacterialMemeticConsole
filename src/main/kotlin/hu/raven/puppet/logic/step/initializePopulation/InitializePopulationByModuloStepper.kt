package hu.raven.puppet.logic.step.initializePopulation

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.asPermutation

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
            population = if (algorithmState.task.costGraph.objectives.size != 1)
                ArrayList(List((algorithmState.task.costGraph.objectives.size + algorithmState.task.transportUnits.size - 1)) { specimenIndex ->
                    OnePartRepresentation(
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
                    OnePartRepresentation(
                        0,
                        1,
                        intArrayOf(0).asPermutation(),
                        true,
                        null,
                        0,
                        0
                    )
                )

            population.forEachIndexed { instanceIndex, instance ->
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
        instance: OnePartRepresentation<C>,
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
        instance.permutation.setEach { index, _ ->
            newPermutation[index]
        }
        instance.inUse = true
        instance.cost = null
    }
}