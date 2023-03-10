package hu.raven.puppet.logic.step.selectsurvivers

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.utility.extention.slice

class SelectSurvivors<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<S, C>() {
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