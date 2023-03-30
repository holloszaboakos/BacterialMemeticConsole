package hu.raven.puppet.logic.step.initializePopulation

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates

class InitializePopulationByModuloStepper<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<S, C>,
) :
    InitializePopulation<S, C>() {

    override fun invoke() {
        algorithmState.run {
            val sizeOfPermutation =
                (task.costGraph.objectives.size + task.transportUnits.size - 1)
            val basePermutation = IntArray(sizeOfPermutation) { it }
            population = if (task.costGraph.objectives.size != 1)
                ArrayList(List(parameters.sizeOfPopulation) { specimenIndex ->
                    subSolutionFactory.produce(
                        specimenIndex,
                        Array(task.transportUnits.size) { index ->
                            if (index == 0)
                                IntArray(task.costGraph.objectives.size) { it }
                            else
                                intArrayOf()
                        }
                    )
                })
            else arrayListOf(
                subSolutionFactory.produce(
                    0,
                    arrayOf(IntArray(task.costGraph.objectives.size) { it })
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
        instance: S,
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
        instance.setData(List(breakPoints.size - 1) {
            newPermutation.slice((breakPoints[it] + 1) until breakPoints[it + 1]).toIntArray()
        })
        instance.iteration = 0
        instance.inUse = true
        instance.cost = null
    }
}