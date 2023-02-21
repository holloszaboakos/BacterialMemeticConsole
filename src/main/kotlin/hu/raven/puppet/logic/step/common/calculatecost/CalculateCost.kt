package hu.raven.puppet.logic.step.common.calculatecost

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.logic.step.AlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.inject

sealed class CalculateCost<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : AlgorithmStep<S, C>() {
    val statistics: BacterialAlgorithmStatistics by inject()

    abstract operator fun invoke(specimen: ISpecimenRepresentation<C>)
}