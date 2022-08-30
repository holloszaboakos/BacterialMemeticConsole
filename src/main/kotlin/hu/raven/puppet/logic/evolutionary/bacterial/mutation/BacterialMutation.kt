package hu.raven.puppet.logic.evolutionary.bacterial.mutation

import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import org.koin.java.KoinJavaComponent

sealed interface BacterialMutation<S : ISpecimenRepresentation> {

    val algorithm: BacterialAlgorithm<S>

    fun calcCostOfEachAndSort(clones: MutableList<S>) {
        val calculateCostOf: CalculateCost<S> by KoinJavaComponent.inject(CalculateCost::class.java)

        clones
            .onEach { calculateCostOf(it) }
            .sortBy { it.cost }
    }

    suspend operator fun invoke()
}