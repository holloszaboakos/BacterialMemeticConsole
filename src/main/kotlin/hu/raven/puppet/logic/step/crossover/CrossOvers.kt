package hu.raven.puppet.logic.step.crossover

import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlinx.coroutines.runBlocking


class CrossOvers<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>,
    val crossoverOperator: CrossOverOperator<C>
){

    operator fun invoke() = runBlocking {
        val children = algorithmState.population
            .filter { !it.inUse }
            .chunked(2)
        val parent = algorithmState.population
            .filter { it.inUse }
            .shuffled()
            .chunked(2)

        parent.forEachIndexed { index, parentPair ->
            crossoverOperator(Pair(parentPair[0], parentPair[1]), children[index][0])
            crossoverOperator(Pair(parentPair[1], parentPair[0]), children[index][1])
        }

    }
}