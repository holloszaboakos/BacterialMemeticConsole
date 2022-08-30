package hu.raven.puppet.logic.common.initialize

import hu.raven.puppet.logic.AAlgorithm4VRP
import hu.raven.puppet.logic.common.logging.DoubleLogger
import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.runIfInstanceOf
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent

class InitializeBacterialAlgorithm : InitializeAlgorithm {
    val logger: DoubleLogger by KoinJavaComponent.inject(DoubleLogger::class.java)

    override fun <S : ISpecimenRepresentation> invoke(algorithm: AAlgorithm4VRP<S>) =
        algorithm.runIfInstanceOf<BacterialAlgorithm<S>> {
            when (state) {
                AAlgorithm4VRP.State.CREATED -> {
                    logger("initializePopulation")
                    initializePopulation()
                    logger("orderByCost")
                    runBlocking { orderPopulationByCost() }
                    logger("orderedByCost")
                    copyOfBest = subSolutionFactory.copy(population.first())
                    copyOfWorst = subSolutionFactory.copy(population.last())
                    state = AAlgorithm4VRP.State.INITIALIZED
                }
                else -> {
                }
            }
        }
}