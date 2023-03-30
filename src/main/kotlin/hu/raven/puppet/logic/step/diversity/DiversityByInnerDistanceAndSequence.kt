package hu.raven.puppet.logic.step.diversity

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import kotlinx.coroutines.runBlocking


class DiversityByInnerDistanceAndSequence<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,

    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val sizeOfPopulation: Int,
    override val iterationLimit: Int,
    override val geneCount: Int,
    override val statistics: BacterialAlgorithmStatistics
) : Diversity<S, C>() {

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