package hu.raven.puppet.logic.evolutionary.common.diversity

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.inject
import kotlin.math.abs

class DiversityByMatrixDistanceFromBest<S : ISpecimenRepresentation>(
    override val algorithm: SEvolutionaryAlgorithm<S>
) : Diversity<S> {

    val statistics: BacterialAlgorithmStatistics by inject(BacterialAlgorithmStatistics::class.java)
    override fun invoke(): Unit = runBlocking {
        val best = algorithm.copyOfBest!!
        val matrixOfBest = preceditionMatrixWithDistance(best)
        statistics.diversity = 0.0

        algorithm.population
            .map {
                CoroutineScope(Dispatchers.IO).launch {
                    val matrix = preceditionMatrixWithDistance(it)
                    val distance = distanceOfSpecimen(matrixOfBest, matrix)
                    synchronized(statistics) {
                        statistics.diversity += distance
                    }
                }
            }
            .forEach { it.join() }
    }

    private fun distanceOfSpecimen(
        matrixFrom: Array<IntArray>,
        matrixTo: Array<IntArray>
    ): Long {
        var distance = 0L
        for (fromIndex in matrixFrom.indices) {
            for (toIndex in matrixFrom[fromIndex].indices) {
                distance += abs(
                    matrixFrom[fromIndex][toIndex] -
                            matrixTo[fromIndex][toIndex]
                )
            }
        }
        return distance
    }

    private fun <S : ISpecimenRepresentation> preceditionMatrixWithDistance(
        specimen: S
    ): Array<IntArray> {
        val inverse = specimen.inverseOfPermutation()
        return Array(inverse.size) { fromIndex ->
            IntArray(inverse.size) { toIndex ->
                inverse[fromIndex] - inverse[toIndex]
            }
        }
    }
}