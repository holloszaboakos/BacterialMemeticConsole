package hu.raven.puppet.logic.common.initialize

import hu.raven.puppet.logic.AAlgorithm4VRP
import hu.raven.puppet.logic.common.logging.DoubleLogger
import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.evolutionary.common.OrderPopulationByCost
import hu.raven.puppet.logic.evolutionary.common.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.runIfInstanceOf
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.inject

class InitializeBacterialAlgorithm<S : ISpecimenRepresentation>(
    override val algorithm: AAlgorithm4VRP<S>
) : InitializeAlgorithm<S> {
    val logger: DoubleLogger by inject(DoubleLogger::class.java)
    val initializePopulation: InitializePopulation<S> by inject(InitializePopulation::class.java)
    val orderPopulationByCost: OrderPopulationByCost<S> by inject(OrderPopulationByCost::class.java)

    override fun invoke() =
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