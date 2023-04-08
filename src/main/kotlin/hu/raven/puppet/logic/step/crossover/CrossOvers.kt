package hu.raven.puppet.logic.step.crossover

import hu.raven.puppet.logic.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class CrossOvers<C : PhysicsUnit<C>>(
    val crossoverOperator: CrossOverOperator<C>
) : EvolutionaryAlgorithmStep<C> {

    override operator fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
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
            children[index][0].let {
                it.iteration = state.iteration
                it.cost = null
                it.inUse = true
                if (!it.permutation.checkFormat())
                    throw Error("Invalid specimen!")
            }
            children[index][1].let {
                it.iteration = state.iteration
                it.cost = null
                it.inUse = true
                if (!it.permutation.checkFormat())
                    throw Error("Invalid specimen!")
            }
        }

    }
}