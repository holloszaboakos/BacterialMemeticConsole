package hu.raven.puppet.logic.operator.initialize_virus_population

import hu.akos.hollo.szabo.math.Permutation
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
            .map { subPermutation ->
                VirusSpecimen(
                    genes = subPermutation,
                    lifeForce = null
                )
            }
    }
}