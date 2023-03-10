package hu.raven.puppet.logic.step.crossover

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.runBlocking


class CrossOvers<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<S, C>() {
    val crossoverOperator: CrossOverOperator<S, C> by inject()

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