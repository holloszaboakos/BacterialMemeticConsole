package hu.raven.puppet.logic.evolutionary.common.iteration

import hu.raven.puppet.logic.AAlgorithm4VRP
import hu.raven.puppet.logic.common.logging.DoubleLogger
import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.evolutionary.common.OrderPopulationByCost
import hu.raven.puppet.logic.evolutionary.common.boost.Boost
import hu.raven.puppet.logic.evolutionary.genetic.CrossOvers
import hu.raven.puppet.logic.evolutionary.genetic.SelectSurvivors
import hu.raven.puppet.logic.evolutionary.genetic.mutatechildren.MutateChildren
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.runIfInstanceOf
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.inject
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class GeneticIteration<S : ISpecimenRepresentation>(
    override val algorithm: SEvolutionaryAlgorithm<S>
) : EvolutionaryIteration<S> {
    val logger: DoubleLogger by inject(DoubleLogger::class.java)

    val orderPopulationByCost: OrderPopulationByCost<S> by inject(OrderPopulationByCost::class.java)
    val boost: Boost<S> by inject(Boost::class.java)
    val selection: SelectSurvivors<S> by inject(SelectSurvivors::class.java)
    val crossover: CrossOvers<S> by inject(CrossOvers::class.java)
    val mutate: MutateChildren<S> by inject(MutateChildren::class.java)


    override fun invoke(
        manageLifeCycle: Boolean
    ) = runBlocking {
        algorithm.runIfInstanceOf<GeneticAlgorithm<S>> {
            if (manageLifeCycle)
                state = AAlgorithm4VRP.State.RESUMED

            runAndLogTime("selection") {
                selection()
            }
            runAndLogTime("crossover") {
                crossover()
            }
            runAndLogTime("mutate") {
                mutate()
            }
            runAndLogTime("orderPopulationByCost") {
                orderPopulationByCost()
            }
            runAndLogTime("boost") {
                boost()
            }

            copyOfBest = subSolutionFactory.copy(population.first())
            copyOfWorst = subSolutionFactory.copy(population.last())
            iteration++
            if (manageLifeCycle)
                state = AAlgorithm4VRP.State.INITIALIZED
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun runAndLogTime(name: String, action: suspend () -> Unit) {
        measureTime {
            action()
        }.let {
            logger("$name time: $it")
        }
    }
}