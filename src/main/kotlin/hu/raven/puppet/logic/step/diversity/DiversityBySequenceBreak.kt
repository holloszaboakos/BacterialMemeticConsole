package hu.raven.puppet.logic.step.diversity

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class DiversityBySequenceBreak<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<S, C>
) : Diversity<S, C>() {

    override fun invoke(): Double = runBlocking {
        val best = algorithmState.copyOfBest!!
        val sequentialOfBest = best.sequentialOfPermutation()
        var diversity = 0.0

        algorithmState.population
            .map {
                CoroutineScope(Dispatchers.Default).launch {
                    val sequential = it.sequentialOfPermutation()
                    val distance = distanceOfSpecimen(sequentialOfBest, sequential)
                    diversity += distance
                }
            }
            .forEach { it.join() }

        diversity
    }

    private fun distanceOfSpecimen(
        sequentialFrom: IntArray,
        sequentialTo: IntArray
    ): Long {
        var distance = 0L
        for (index in sequentialFrom.indices) {
            if (sequentialFrom[index] != sequentialTo[index])
                distance++
        }
        return distance
    }
}