package hu.raven.puppet.logic.step.bacterialmutation

import hu.raven.puppet.logic.step.bacterialmutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics

sealed class BacterialMutation<C : PhysicsUnit<C>>{
    protected abstract val statistics: BacterialAlgorithmStatistics
    protected abstract val mutationOnSpecimen: MutationOnSpecimen<C>

    abstract suspend operator fun invoke()
}