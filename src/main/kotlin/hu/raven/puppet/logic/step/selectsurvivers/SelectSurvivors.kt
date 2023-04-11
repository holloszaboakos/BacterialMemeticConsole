package hu.raven.puppet.logic.step.selectsurvivers

import hu.raven.puppet.logic.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.slice

class SelectSurvivors<C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<C> {
    override operator fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
        population.deactivateAll()
        population.mapActives { it }.withIndex().asSequence()
            .slice(0 until population.activeCount / 4)
            .forEach { population.activate(it.index) }

        population.mapActives { it }.withIndex().asSequence()
            .slice(population.activeCount / 4 until population.activeCount)
            .shuffled()
            .slice(0 until population.activeCount / 4)
            .forEach { population.activate(it.index) }
    }
}