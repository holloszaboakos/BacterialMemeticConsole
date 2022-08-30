package hu.raven.puppet.logic.evolutionary.common.initializePopulation

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class InitializePopulationByModuloStepper : InitializePopulation {
    override fun <S : ISpecimenRepresentation> invoke(algorithm: SEvolutionaryAlgorithm<S>) {
        algorithm.run {
            val sizeOfPermutation = costGraph.objectives.size + salesmen.size - 1
            val basePermutation = IntArray(sizeOfPermutation) { it }
            population = if (costGraph.objectives.size != 1)
                ArrayList(List(sizeOfPopulation) { specimenIndex ->
                    subSolutionFactory.produce(
                        specimenIndex,
                        Array(salesmen.size) { index ->
                            if (index == 0)
                                IntArray(costGraph.objectives.size) { it }
                            else
                                intArrayOf()
                        }
                    )
                })
            else arrayListOf(
                subSolutionFactory.produce(
                    0,
                    arrayOf(IntArray(costGraph.objectives.size) { it })
                )
            )

            population.forEachIndexed { instanceIndex, instance ->
                val step = instanceIndex % (sizeOfPermutation - 1) + 1
                if (step == 1) {
                    basePermutation.shuffle()
                }

                val newContains = BooleanArray(sizeOfPermutation) { false }
                val newPermutation = IntArray(sizeOfPermutation) { -1 }
                var baseIndex = step
                for (newIndex in 0 until sizeOfPermutation) {
                    if (newContains[basePermutation[baseIndex]])
                        baseIndex = (baseIndex + 1) % sizeOfPermutation
                    newPermutation[newIndex] = basePermutation[baseIndex]
                    newContains[basePermutation[baseIndex]] = true
                    baseIndex = (baseIndex + step) % sizeOfPermutation
                }

                val breakPoints = newPermutation
                    .mapIndexed { index, value ->
                        if (value < costGraph.objectives.size)
                            -1
                        else
                            index
                    }
                    .filter { it != -1 }
                    .toMutableList()

                breakPoints.add(0, -1)
                breakPoints.add(sizeOfPermutation)
                instance.setData(List(breakPoints.size - 1) {
                    newPermutation.slice((breakPoints[it] + 1) until breakPoints[it + 1]).toIntArray()
                })
                instance.iteration = 0
                instance.costCalculated = false
                instance.inUse = true
                instance.cost = -1.0
            }
        }
    }
}