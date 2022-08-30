package hu.raven.puppet.logic.common.initialize

import hu.raven.puppet.logic.AAlgorithm4VRP
import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.evolutionary.common.OrderPopulationByCost
import hu.raven.puppet.logic.evolutionary.common.boost.Boost
import hu.raven.puppet.logic.evolutionary.common.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.runIfInstanceOf
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.inject

class InitializeGeneticAlgorithm<S : ISpecimenRepresentation>(
    override val algorithm: AAlgorithm4VRP<S>
) : InitializeAlgorithm<S> {
    val initializePopulation: InitializePopulation<S> by inject(InitializePopulation::class.java)
    val orderPopulationByCost: OrderPopulationByCost<S> by inject(OrderPopulationByCost::class.java)
    val boost: Boost<S> by inject(Boost::class.java)

    override fun invoke() =
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