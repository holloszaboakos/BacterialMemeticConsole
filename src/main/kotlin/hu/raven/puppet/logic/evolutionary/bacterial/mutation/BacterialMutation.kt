package hu.raven.puppet.logic.evolutionary.bacterial.mutation

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface BacterialMutation {

    fun <S : ISpecimenRepresentation> BacterialAlgorithm<S>.calcCostOfEachAndSort(clones: MutableList<S>) {
        clones
            .onEach { calculateCostOf(it) }
            .sortBy { it.cost }
    }

    suspend operator fun <S : ISpecimenRepresentation> invoke(
        algorithm: BacterialAlgorithm<S>
    )
}