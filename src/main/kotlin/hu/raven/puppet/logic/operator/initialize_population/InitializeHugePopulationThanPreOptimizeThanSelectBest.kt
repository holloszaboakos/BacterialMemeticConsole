package hu.raven.puppet.logic.operator.initialize_population

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray.Companion.size
import hu.akos.hollo.szabo.collections.slice
import hu.akos.hollo.szabo.math.asPermutation
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.length
import hu.raven.puppet.logic.operator.bacterial_mutation_on_specimen.MutationOnSpecimen
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId

class InitializeHugePopulationThanPreOptimizeThanSelectBest(
    private val sizeOfPopulation: Int,
    private val sizeOfTask: Int,
    private val mutationOperator: MutationOnSpecimen,
) : InitializePopulation {


    override fun invoke(): List<OnePartRepresentationWithCostAndIterationAndId> {
        val basePermutation = IntArray(sizeOfTask) { it }

        var population = createPopulation(sizeOfTask)

        population.asSequence().forEachIndexed { instanceIndex, instance ->
            val step = instanceIndex % (sizeOfTask - 1) + 1
            if (step == 1) {
                basePermutation.shuffle()
            }

            val newContains = BooleanArray(sizeOfTask) { false }
            val newPermutation = IntArray(sizeOfTask) { -1 }
            var baseIndex = step
            for (newIndex in 0..<sizeOfTask) {
                if (newContains[basePermutation[baseIndex]])
                    baseIndex = (baseIndex + 1) % sizeOfTask
                newPermutation[newIndex] = basePermutation[baseIndex]
                newContains[basePermutation[baseIndex]] = true
                baseIndex = (baseIndex + step) % sizeOfTask
            }
            newPermutation.forEachIndexed { index, value ->
                instance.permutation[index] = value
            }
            instance.iterationOfCreation = 0
            instance.cost = null
        }

        val bestImprovements = bacterialMutateEach(population)

        population =
            bestImprovements
                .map { it.first }
                .slice(0..<sizeOfPopulation)
                .map { specimen ->
                    OnePartRepresentationWithCostAndIterationAndId(
                        id = specimen.value.id,
                        iterationOfCreation = specimen.value.iterationOfCreation,
                        cost = specimen.value.cost,
                        permutation = specimen.value.permutation.clone()
                    )
                }.toList()
        return population
    }

    private fun bacterialMutateEach(population: List<OnePartRepresentationWithCostAndIterationAndId>) = population
        .asSequence()
        .withIndex()
        .map { specimenWithIndex ->
            Pair(
                specimenWithIndex.copy(),
                specimenWithIndex.apply { mutationOperator(specimenWithIndex, 0) }
            )
        }
        .sortedBy {
            it.first.value.costOrException().length() / it.second.value.costOrException().length()
        }


    private fun createPopulation(sizeOfTask: Int): List<OnePartRepresentationWithCostAndIterationAndId> {
        return if (sizeOfTask != 1)
            MutableList((sizeOfTask)) {
                OnePartRepresentationWithCostAndIterationAndId(
                    id = it,
                    iterationOfCreation = 0,
                    cost = null,
                    permutation = IntArray(sizeOfTask) { index -> index }
                        .asPermutation()
                )
            }
        else
            mutableListOf(
                OnePartRepresentationWithCostAndIterationAndId(
                    id = 0,
                    iterationOfCreation = 0,
                    cost = null,
                    intArrayOf(0).asPermutation()
                )
            )
    }
}