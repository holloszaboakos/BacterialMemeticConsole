package hu.raven.puppet.logic.operator.diversity_of_population

import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


data object DiversityOfPopulationBySequenceBreak : DiversityOfPopulation {

    override fun invoke(algorithmState: EvolutionaryAlgorithmState): Double = runBlocking {
        val best = algorithmState.copyOfBest ?: throw Exception("Algorithm didn't determine best solution yet!")
        var diversity = 0.0

        algorithmState.population.activesAsSequence()
            .map {
                CoroutineScope(Dispatchers.Default).launch {
                    val distance = distanceOfSpecimen(best.permutation, it.permutation)
                    diversity += distance
                }
            }
            .forEach { it.join() }

        diversity
    }

    private fun distanceOfSpecimen(
        bestPermutation: Permutation,
        toPermutation: Permutation
    ): Long {
        var distance = 0L
        for (index in 0..bestPermutation.size) {
            if (bestPermutation.after(index) != toPermutation.after(index))
                distance++
        }
        return distance
    }
}