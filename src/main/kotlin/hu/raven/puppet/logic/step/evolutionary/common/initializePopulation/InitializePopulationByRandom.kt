package hu.raven.puppet.logic.step.evolutionary.common.initializePopulation

import hu.raven.puppet.logic.specimen.DOnePartRepresentation
import hu.raven.puppet.logic.specimen.DTwoPartRepresentation
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.physics.PhysicsUnit
import kotlin.random.Random
import kotlin.random.nextInt

class InitializePopulationByRandom<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : InitializePopulation<S, C>() {

    override fun invoke() {
        algorithmState.population = if (taskHolder.task.costGraph.objectives.size != 1)
            ArrayList(List(sizeOfPopulation) { specimenIndex ->
                subSolutionFactory.produce(
                    specimenIndex,
                    Array(taskHolder.task.salesmen.size) { index ->
                        if (index == 0)
                            IntArray(taskHolder.task.costGraph.objectives.size) { it }
                        else
                            intArrayOf()
                    }
                )
            })
        else arrayListOf(
            subSolutionFactory.produce(
                0,
                arrayOf(IntArray(taskHolder.task.costGraph.objectives.size) { it })
            )
        )

        algorithmState.population.forEach { permutation ->
            permutation.shuffle()
            var length = 0
            when (permutation) {
                is DTwoPartRepresentation<*> ->
                    permutation.forEachSliceIndexed { index, _ ->
                        if (index == permutation.sliceLengths.size - 1) {
                            permutation.sliceLengths[index] = taskHolder.task.costGraph.objectives.size - length

                        } else {
                            permutation.sliceLengths[index] =
                                Random.nextInt(0..(taskHolder.task.costGraph.objectives.size - length))
                            length += permutation.sliceLengths[index]
                        }
                    }

                is DOnePartRepresentation<*> -> {

                }
            }
            permutation.iteration = 0
            permutation.cost = null
            permutation.inUse = true
        }
    }
}