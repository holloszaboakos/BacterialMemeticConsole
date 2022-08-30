package hu.raven.puppet.logic.evolutionary.genetic

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.extention.slice

class SelectSurvivors<S : ISpecimenRepresentation>(
    val algorithm: GeneticAlgorithm<S>
) {
    operator fun invoke() {
        algorithm.run {
            population.asSequence()
                .slice(0 until population.size / 4)
                .forEach { it.inUse = true }

            population.asSequence()
                .slice(population.size / 4 until population.size)
                .shuffled()
                .slice(0 until population.size / 4)
                .forEach { it.inUse = true }
        }
    }
}