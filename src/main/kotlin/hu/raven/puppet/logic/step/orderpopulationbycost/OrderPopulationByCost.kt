package hu.raven.puppet.logic.step.orderpopulationbycost

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


class OrderPopulationByCost<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val taskHolder: VRPTaskHolder,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val sizeOfPopulation: Int,
    override val iterationLimit: Int,
    override val geneCount: Int
) : EvolutionaryAlgorithmStep<S, C>() {
    val calculateCostOf: CalculateCost<S, C> by inject()

    suspend operator fun invoke(
    ) = withContext(Dispatchers.Default) {
        algorithmState.run {
            population.asFlow()
                .filter { it.cost == null }
                .map { specimen ->
                    calculateCostOf(specimen)
                }.collect()

            population.sortBy { it.costOrException().value }

            population.forEachIndexed { index, it ->
                it.orderInPopulation = index
                it.inUse = false
            }
        }
    }
}