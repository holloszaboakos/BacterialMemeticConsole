package hu.raven.puppet.logic.operator.initialize_population

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray.Companion.size
import hu.akos.hollo.szabo.math.asPermutation
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.task.Task

class InitializePopulationByModuloStepper(
    private val sizeOfPopulation: Int
) : InitializePopulation {

    override fun invoke(task: Task): List<OnePartRepresentationWithCostAndIterationAndId> {
        val sizeOfPermutation =
            if (task.transportUnits.size != 0)
                (task.costGraph.objectives.size + task.transportUnits.size - 1)
            else
                task.costGraph.objectives.size
        val basePermutation = IntArray(sizeOfPermutation) { it }
        val population = if (task.costGraph.objectives.size != 1)
            MutableList(sizeOfPopulation) {
                OnePartRepresentationWithCostAndIterationAndId(
                    id = it,
                    iterationOfCreation = 0,
                    cost = null,
                    objectiveCount = task.costGraph.objectives.size,
                    permutation = IntArray(
                        task.transportUnits.size +
                                task.costGraph.objectives.size
                    ) { index ->
                        index
                    }.asPermutation()
                )
            }
        else
            mutableListOf(
                OnePartRepresentationWithCostAndIterationAndId(
                    0,
                    0,
                    null,
                    1,
                    intArrayOf(0).asPermutation()
                )
            )

        population.forEachIndexed { instanceIndex, instance ->
            initSpecimen(
                instanceIndex,
                instance,
                sizeOfPermutation,
                basePermutation
            )
        }
        return population
    }

    //TODO: review
    private fun initSpecimen(
        instanceIndex: Int,
        instance: OnePartRepresentationWithCostAndIterationAndId,
        sizeOfPermutation: Int,
        basePermutation: IntArray
    ) {
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

        instance.permutation.clear()
        newPermutation.forEachIndexed { index, value ->
            instance.permutation[index] = value
        }
        instance.cost = null
    }
}