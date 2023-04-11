package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class BoostOnFirstThatImproved<C : PhysicsUnit<C>>(
    override val boostOperator: BoostOperator<C>
) : Boost<C>() {
    var costPerPermutation = mutableListOf<C?>()

    override fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
        if (costPerPermutation.isEmpty()) {
            costPerPermutation = MutableList(population.activeCount) { null }
        }

        population.activesAsSequence()
            .firstOrNull {
                costPerPermutation[it.id] != null && it.content.costOrException() < costPerPermutation[it.id]!!
            }
            ?.let {
                costPerPermutation[it.id] = it.content.cost
                boostOperator(it)
            }
    }
}