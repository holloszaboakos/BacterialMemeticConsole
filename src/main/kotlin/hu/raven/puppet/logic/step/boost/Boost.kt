package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics

sealed class Boost<C : PhysicsUnit<C>> {
    abstract val boostOperator: BoostOperator<C>
    abstract val statistics: BacterialAlgorithmStatistics
    abstract suspend operator fun invoke()
}