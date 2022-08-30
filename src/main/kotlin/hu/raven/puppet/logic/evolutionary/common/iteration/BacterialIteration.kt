package hu.raven.puppet.logic.evolutionary.common.iteration

import hu.raven.puppet.logic.AAlgorithm4VRP
import hu.raven.puppet.logic.common.logging.DoubleLogger
import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.evolutionary.bacterial.genetransfer.GeneTransfer
import hu.raven.puppet.logic.evolutionary.bacterial.mutation.BacterialMutation
import hu.raven.puppet.logic.evolutionary.common.OrderPopulationByCost
import hu.raven.puppet.logic.evolutionary.common.boost.Boost
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.runIfInstanceOf
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.inject
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class BacterialIteration<S : ISpecimenRepresentation>(
    override val algorithm: SEvolutionaryAlgorithm<S>
) : EvolutionaryIteration<S> {
    val logger: DoubleLogger by inject(DoubleLogger::class.java)

    val boost: Boost<S> by inject(Boost::class.java)
    val geneTransfer: GeneTransfer<S> by inject(GeneTransfer::class.java)
    val mutate: BacterialMutation<S> by inject(BacterialMutation::class.java)
    val orderPopulationByCost: OrderPopulationByCost<S> by inject(OrderPopulationByCost::class.java)

    override fun invoke(
        manageLifeCycle: Boolean
    ) = runBlocking {
        algorithm.runIfInstanceOf<BacterialAlgorithm<S>> {
            if (manageLifeCycle)
                state = AAlgorithm4VRP.State.RESUMED

            runAndLogTime("boost") {
                boost()
            }
            runAndLogTime("geneTransfer") {
                geneTransfer()
            }
            runAndLogTime("mutate") {
                mutate()
            }
            runAndLogTime("orderPopulationByCost") {
                orderPopulationByCost()
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