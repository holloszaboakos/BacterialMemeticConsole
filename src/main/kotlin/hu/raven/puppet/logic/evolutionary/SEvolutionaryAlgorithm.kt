package hu.raven.puppet.logic.evolutionary

import hu.raven.puppet.logic.AAlgorithm4VRP
import hu.raven.puppet.logic.evolutionary.setup.SEvolutionaryAlgorithmSetup
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed class SEvolutionaryAlgorithm<S : ISpecimenRepresentation>(
    val iterationLimit: Int,
    val sizeOfPopulation: Int
) : AAlgorithm4VRP<S>() {

    abstract override val setup: SEvolutionaryAlgorithmSetup

    var iteration = 0
    var population: ArrayList<S> = arrayListOf()
    var copyOfBest: S? = null
    var copyOfWorst: S? = null

    fun iterate(manageLifeCycle: Boolean) = setup.iteration(this, manageLifeCycle)
    fun initializePopulation() = setup.initializePopulation(this)
    suspend fun orderPopulationByCost() = setup.orderByCost(this)
    suspend fun boost() = setup.boost(this)
}