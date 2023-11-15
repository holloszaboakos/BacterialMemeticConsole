package hu.raven.puppet.logic.operator.initializeVirusPopulation

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.solution.VirusSpecimen
import kotlin.random.Random

class BasicInitializationOfVirusPopulation(
    private val sizeOfPopulation: Int,
    private val sizeOfVirus: Int,
) : InitializeVirusPopulation {
    override fun invoke(permutations: List<Permutation>): List<VirusSpecimen> {
        val subPermutations: List<IntArray> = (0 until sizeOfPopulation)
            .map {
                val sourcePermutation = permutations.random()
                val randomStart = Random.nextInt(sourcePermutation.size - sizeOfVirus + 1)
                sourcePermutation
                    .slice(randomStart..<(randomStart + sizeOfVirus))
                    .toIntArray()
            }

        return subPermutations
            .mapIndexed { index, subPermutation ->
                VirusSpecimen(
                    id = index,
                    genes = subPermutation,
                    lifeForce = null
                )
            }
    }
}