package hu.raven.puppet.logic.step.crossover

import hu.raven.puppet.logic.EvolutionaryAlgorithmStepFactory
import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class CrossOversFactory<C : PhysicsUnit<C>>(
    val crossoverOperator: CrossOverOperator<C>
) : EvolutionaryAlgorithmStepFactory<C> {

    override operator fun invoke() =
        fun EvolutionaryAlgorithmState<C>.() {
            val children = population
                .filter { !it.inUse }
                .chunked(2)
            val parent = population
                .filter { it.inUse }
                .shuffled()
                .chunked(2)

            parent.forEachIndexed { index, parentPair ->
                crossoverOperator(Pair(parentPair[0], parentPair[1]), children[index][0])
                crossoverOperator(Pair(parentPair[1], parentPair[0]), children[index][1])
            }

        }
}