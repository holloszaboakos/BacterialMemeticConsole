package hu.raven.puppet.logic.step.bacterialmutation

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.bacterialmutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics

sealed class BacterialMutation<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<S, C>() {
    protected abstract val mutationPercentage: Float
    protected abstract val statistics: BacterialAlgorithmStatistics
    protected abstract val mutationOnSpecimen: MutationOnSpecimen<S, C>

    abstract suspend operator fun invoke()
}