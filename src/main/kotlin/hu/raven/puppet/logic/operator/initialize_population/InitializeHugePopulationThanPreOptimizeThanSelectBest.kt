package hu.raven.puppet.logic.operator.initialize_population

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray.Companion.size
import hu.akos.hollo.szabo.collections.slice
import hu.akos.hollo.szabo.math.asPermutation
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.length
import hu.raven.puppet.logic.operator.bacterial_mutation_on_specimen.MutationOnSpecimen
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.task.Task

class InitializeHugePopulationThanPreOptimizeThanSelectBest(
    private val sizeOfPopulation: Int,
    private val mutationOperator: MutationOnSpecimen,
) : InitializePopulation {


    override fun invoke(task: Task): List<OnePartRepresentationWithCostAndIterationAndId> {
        val sizeOfPermutation = task.costGraph.objectives.size + task.transportUnits.size - 1
        val basePermutation = IntArray(sizeOfPermutation) { it }

        var population = createPopulation(task)

        population.asSequence().forEachIndexed { instanceIndex, instance ->
            val step = instanceIndex % (sizeOfPermutation - 1) + 1
            if (step == 1) {
                basePermutation.shuffle()
            }

            val newContains = BooleanArray(sizeOfPermutation) { false }
            val newPermutation = IntArray(sizeOfPermutation) { -1 }
            var baseIndex = step
            for (newIndex in 0..<sizeOfPermutation) {
                if (newContains[basePermutation[baseIndex]])
                    baseIndex = (baseIndex + 1) % sizeOfPermutation
                newPermutation[newIndex] = basePermutation[baseIndex]
                newContains[basePermutation[baseIndex]] = true
                baseIndex = (baseIndex + step) % sizeOfPermutation
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
                        objectiveCount = specimen.value.objectiveCount,
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


    private fun createPopulation(task: Task): List<OnePartRepresentationWithCostAndIterationAndId> {
        return if (task.costGraph.objectives.size != 1)
            MutableList((task.costGraph.objectives.size + task.transportUnits.size - 1)) {
                OnePartRepresentationWithCostAndIterationAndId(
                    id = it,
                    iterationOfCreation = 0,
                    cost = null,
                    objectiveCount = task.costGraph.objectives.size,
                    permutation = IntArray(
                        task.transportUnits.size +
                                task.costGraph.objectives.size
                    ) { index -> index }
                        .asPermutation()
                )
            }
        else
            mutableListOf(
                OnePartRepresentationWithCostAndIterationAndId(
                    id = 0,
                    iterationOfCreation = 0,
                    cost = null,
                    1,
                    intArrayOf(0).asPermutation()
                )
            )
    }
}