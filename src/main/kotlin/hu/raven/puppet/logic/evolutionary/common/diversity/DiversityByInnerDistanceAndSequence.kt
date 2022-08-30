package hu.raven.puppet.logic.evolutionary.common.diversity

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.Statistics
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent

class DiversityByInnerDistanceAndSequence : Diversity {
    val statistics: Statistics by KoinJavaComponent.inject(Statistics::class.java)
    override fun <S : ISpecimenRepresentation> invoke(algorithm: SEvolutionaryAlgorithm<S>): Unit = runBlocking {
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