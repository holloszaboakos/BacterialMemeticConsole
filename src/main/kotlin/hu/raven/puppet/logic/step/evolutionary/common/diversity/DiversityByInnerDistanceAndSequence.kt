package hu.raven.puppet.logic.step.evolutionary.common.diversity

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlinx.coroutines.runBlocking


class DiversityByInnerDistanceAndSequence<S : ISpecimenRepresentation> : Diversity<S>() {

    override fun invoke(): Unit = runBlocking {
        statistics.diversity = 0.0

        val sequentials = algorithmState.population.map { it.sequentialOfPermutation() }


        sequentials.forEach { firstSequential ->
            sequentials.forEach { secondSequential ->
                val distance = distanceOfSpecimen(firstSequential, secondSequential)
                synchronized(statistics) {
                    statistics.diversity += distance
                }
            }
        }

        statistics.diversity /= (sizeOfPopulation * sizeOfPopulation)
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