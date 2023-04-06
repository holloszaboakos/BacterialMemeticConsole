package hu.raven.puppet.logic.step.selectsurvivers

import hu.raven.puppet.logic.EvolutionaryAlgorithmStepFactory
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.slice

class SelectSurvivorsFactory<C : PhysicsUnit<C>> : EvolutionaryAlgorithmStepFactory<C> {
    override operator fun invoke() =
        fun EvolutionaryAlgorithmState<C>.() {
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