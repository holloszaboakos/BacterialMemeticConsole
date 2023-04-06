package hu.raven.puppet.logic.step.crossover

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import kotlinx.coroutines.runBlocking


class CrossOvers<C : PhysicsUnit<C>>(
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<C>,
    val crossoverOperator: CrossOverOperator<C>
) : EvolutionaryAlgorithmStep<C>() {

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