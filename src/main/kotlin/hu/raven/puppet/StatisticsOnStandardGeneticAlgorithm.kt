package hu.raven.puppet

import hu.raven.puppet.logic.iteration.AlgorithmIteration
import hu.raven.puppet.logic.iteration.EvolutionaryAlgorithmIteration
import hu.raven.puppet.logic.step.booststrategy.BoostStrategy
import hu.raven.puppet.logic.step.crossoverstrategy.CrossOverStrategy
import hu.raven.puppet.logic.step.mutatechildren.MutateChildren
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.logic.step.selectsurvivers.SelectSurvivors
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

private val TASK_SIZES = arrayOf(4, 8, 16, 32, 64, 128, 256, 512, 1024)

//LAST RUN: 2023-12-22
fun main() {
    startKoin {
        modules(
            module {
                single<AlgorithmIteration<*>> {
                    EvolutionaryAlgorithmIteration(
                        steps = arrayOf(
                            get<BoostStrategy>(),
                            get<SelectSurvivors>(),
                            get<CrossOverStrategy>(),
                            get<MutateChildren>(),
                            get<OrderPopulationByCost>(),
                        )
                    )
                }
            }
        )
    }


    stopKoin()
}