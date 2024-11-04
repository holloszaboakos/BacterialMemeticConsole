package hu.raven.puppet.logic.operator.initialize_population

import hu.akos.hollo.szabo.math.asPermutation
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration

class InitializePopulationByModuloStepper(
    private val sizeOfPopulation: Int,
    private val sizeOfTask: Int,
) : InitializePopulation {

    override fun invoke(): List<OnePartRepresentationWithCostAndIteration> {
        val basePermutation = IntArray(sizeOfTask) { it }
        val population =
            MutableList(sizeOfPopulation) {
                OnePartRepresentationWithCostAndIteration(
                    iterationOfCreation = 0,
                    cost = null,
                    permutation = IntArray(
                        sizeOfTask
                    ) { index ->
                        index
                    }.asPermutation()
                )
            }

        population.forEachIndexed { instanceIndex, instance ->
            initSpecimen(
                instanceIndex,
                instance,
                sizeOfTask,
                basePermutation
            )
        }
        return population
    }

    private fun initSpecimen(
        instanceIndex: Int,
        instance: OnePartRepresentationWithCostAndIteration,
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