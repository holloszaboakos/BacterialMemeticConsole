package hu.raven.puppet.logic.operator.diversity_of_population

import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


data object DiversityOfPopulationByInnerDistanceAndSequence : DiversityOfPopulation {

    override fun invoke(algorithmState: EvolutionaryAlgorithmState<*>): Double = algorithmState.run {
        var diversity = 0.0

        population.activesAsSequence().forEach { firstSpecimen ->
            population.activesAsSequence().forEach { secondSpecimen ->
                val distance = distanceOfSpecimen(
                    firstSpecimen.permutation,
                    secondSpecimen.permutation
                )
                diversity += distance
            }
        }

        diversity /= (population.activeCount * population.activeCount)
        diversity
    }

    private fun distanceOfSpecimen(
        permutationFrom: Permutation,
        permutationTo: Permutation
    ): Long {
        var distance = 0L
        for (index in 0..permutationFrom.size) {
            if (permutationFrom.after(index) != permutationTo.after(index))
                distance++
        }
        return distance
    }
}