package hu.raven.puppet.logic.common.initialize

import hu.raven.puppet.logic.AAlgorithm4VRP
import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.runIfInstanceOf
import kotlinx.coroutines.runBlocking

class InitializeGeneticAlgorithm : InitializeAlgorithm {
    override fun <S : ISpecimenRepresentation> invoke(algorithm: AAlgorithm4VRP<S>) =
        algorithm.runIfInstanceOf<GeneticAlgorithm<S>> {
            when (state) {
                AAlgorithm4VRP.State.CREATED -> {
                    initializePopulation()
                    runBlocking {
                        orderPopulationByCost()
                        boost()
                    }
                    copyOfBest = subSolutionFactory.copy(population.first())
                    copyOfWorst = subSolutionFactory.copy(population.last())
                    state = AAlgorithm4VRP.State.INITIALIZED
                }
                else -> {
                }
            }
        }
}