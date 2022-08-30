package hu.raven.puppet.logic.evolutionary.common.diversity

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.Statistics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.inject

class DiversityBySequenceBreak<S : ISpecimenRepresentation>(
    override val algorithm: SEvolutionaryAlgorithm<S>
) : Diversity<S> {

    val statistics: Statistics by inject(Statistics::class.java)
    override fun invoke(): Unit = runBlocking {
        val best = algorithm.copyOfBest!!
        val sequentialOfBest = best.sequentialOfPermutation()
        statistics.diversity = 0.0

        algorithm.population
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