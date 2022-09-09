package hu.raven.puppet.logic.step.evolutionary.genetic

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.evolutionary.EvolutionaryAlgorithmStep
import hu.raven.puppet.utility.extention.slice

class SelectSurvivors<S : ISpecimenRepresentation> : EvolutionaryAlgorithmStep<S>() {
    operator fun invoke() {
        algorithmState.run {
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