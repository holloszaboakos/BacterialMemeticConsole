package hu.raven.puppet.logic.step.initializePopulation

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.TwoPartRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import kotlin.random.Random
import kotlin.random.nextInt

class InitializePopulationByRandom<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<S, C>,
) : InitializePopulation<S, C>() {

    override fun invoke() {
        algorithmState.population = if (algorithmState.task.costGraph.objectives.size != 1)
            ArrayList(List(parameters.sizeOfPopulation) { specimenIndex ->
                subSolutionFactory.produce(
                    specimenIndex,
                    Array(algorithmState.task.transportUnits.size) { index ->
                        if (index == 0)
                            IntArray(algorithmState.task.costGraph.objectives.size) { it }
                        else
                            intArrayOf()
                    }
                )
            })
        else arrayListOf(
            subSolutionFactory.produce(
                0,
                arrayOf(IntArray(algorithmState.task.costGraph.objectives.size) { it })
            )
        )

        algorithmState.population.forEach { permutation ->
            permutation.shuffle()
            var length = 0
            when (permutation) {
                is TwoPartRepresentation<*> ->
                    permutation.forEachSliceIndexed { index, _ ->
                        if (index == permutation.sliceLengths.size - 1) {
                            permutation.sliceLengths[index] = algorithmState.task.costGraph.objectives.size - length

                        } else {
                            permutation.sliceLengths[index] =
                                Random.nextInt(0..(algorithmState.task.costGraph.objectives.size - length))
                            length += permutation.sliceLengths[index]
                        }
                    }

                is OnePartRepresentation<*> -> throw Exception("OnePartRepresentation not supported")
            }
            permutation.iteration = 0
            permutation.cost = null
            permutation.inUse = true
        }
    }
}