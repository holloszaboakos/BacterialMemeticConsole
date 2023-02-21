package hu.raven.puppet.model.logging

import hu.raven.puppet.model.physics.PhysicsUnit

data class PopulationData<C : PhysicsUnit<C>>(
    val best: SpecimenData<C>,
    val second: SpecimenData<C>?,
    val third: SpecimenData<C>?,
    val worst: SpecimenData<C>,
    val median: SpecimenData<C>,
    val diversity: Double,
    val geneBalance: Double
)
