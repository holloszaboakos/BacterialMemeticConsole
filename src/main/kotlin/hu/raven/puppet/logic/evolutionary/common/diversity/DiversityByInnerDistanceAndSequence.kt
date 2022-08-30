package hu.raven.puppet.logic.evolutionary.common.diversity

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.Statistics
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.inject

class DiversityByInnerDistanceAndSequence<S : ISpecimenRepresentation>(
    override val algorithm: SEvolutionaryAlgorithm<S>
) : Diversity<S> {

    val statistics: Statistics by inject(Statistics::class.java)
    override fun invoke(): Unit = runBlocking {
        statistics.diversity = 0.0

        val sequentials = algorithm.population.map { it.sequentialOfPermutation() }


        sequentials.forEach { firstSequential ->
            sequentials.forEach { secondSequential ->
                val distance = distanceOfSpecimen(firstSequential, secondSequential)
                synchronized(statistics) {
                    statistics.diversity += distance
                }
            }
        }

        statistics.diversity /= (algorithm.sizeOfPopulation * algorithm.sizeOfPopulation)
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