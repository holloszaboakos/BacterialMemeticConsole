package hu.raven.puppet.logic.step.initializePopulation

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.TwoPartRepresentation
import kotlin.random.Random
import kotlin.random.nextInt

class InitializePopulationByRandom<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : InitializePopulation<S, C>() {

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
                is TwoPartRepresentation<*> ->
                    permutation.forEachSliceIndexed { index, _ ->
                        if (index == permutation.sliceLengths.size - 1) {
                            permutation.sliceLengths[index] = taskHolder.task.costGraph.objectives.size - length

                        } else {
                            permutation.sliceLengths[index] =
                                Random.nextInt(0..(taskHolder.task.costGraph.objectives.size - length))
                            length += permutation.sliceLengths[index]
                        }
                    }

                is OnePartRepresentation<*> -> {

                }
            }
            permutation.iteration = 0
            permutation.cost = null
            permutation.inUse = true
        }
    }
}