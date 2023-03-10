package hu.raven.puppet.logic.step.diversity

import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.physics.PhysicsUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class DiversityBySequenceBreak<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : Diversity<S, C>() {

    override fun invoke(): Unit = runBlocking {
        val best = algorithmState.copyOfBest!!
        val sequentialOfBest = best.sequentialOfPermutation()
        statistics.diversity = 0.0

        algorithmState.population
            .map {
                CoroutineScope(Dispatchers.Default).launch {
                    val sequential = it.sequentialOfPermutation()
                    val distance = distanceOfSpecimen(sequentialOfBest, sequential)
                    synchronized(statistics) {
                        statistics.diversity += distance
                    }
                }
            }
            .forEach { it.join() }
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