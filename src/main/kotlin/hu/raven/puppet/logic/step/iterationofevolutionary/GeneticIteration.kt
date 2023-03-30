package hu.raven.puppet.logic.step.iterationofevolutionary

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.boost.Boost
import hu.raven.puppet.logic.step.crossover.CrossOvers
import hu.raven.puppet.logic.step.mutatechildren.MutateChildren
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.logic.step.selectsurvivers.SelectSurvivors
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class GeneticIteration<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,

    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val sizeOfPopulation: Int,
    override val iterationLimit: Int,
    override val geneCount: Int,
    val orderPopulationByCost: OrderPopulationByCost<S, C>,
    val boost: Boost<S, C>,
    val selection: SelectSurvivors<S, C>,
    val crossover: CrossOvers<S, C>,
    val mutate: MutateChildren<S, C>
) : EvolutionaryIteration<S, C>() {


    override fun invoke(): Unit = runBlocking {

        selection()
        crossover()
        mutate()
        orderPopulationByCost()
        boost()
        algorithmState.apply {
            copyOfBest = subSolutionFactory.copy(population.first())
            copyOfWorst = subSolutionFactory.copy(population.last())
            iteration++
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