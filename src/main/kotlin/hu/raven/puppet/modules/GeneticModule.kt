package hu.raven.puppet.modules

import hu.raven.puppet.logic.step.crossover.CrossOvers
import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.step.crossoveroperator.HeuristicCrossOver
import hu.raven.puppet.logic.step.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.step.initialize.InitializeGeneticAlgorithm
import hu.raven.puppet.logic.step.iterationofevolutionary.EvolutionaryIteration
import hu.raven.puppet.logic.step.iterationofevolutionary.GeneticIteration
import hu.raven.puppet.logic.step.mutatechildren.MutateChildren
import hu.raven.puppet.logic.step.mutatechildren.MutateChildrenBySwap
import hu.raven.puppet.logic.step.selectsurvivers.SelectSurvivors
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.model.statistics.GeneticAlgorithmStatistics
import org.koin.core.qualifier.named
import org.koin.dsl.module

val geneticModule = module {
    single(named(AlgorithmParameters.ITERATION_LIMIT)) { 26000 }
    single(named(AlgorithmParameters.SIZE_OF_POPULATION)) { 10_000 }

    factory<InitializeAlgorithm<*, *>> {
        InitializeGeneticAlgorithm<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get(), get(), get(),
        )
    }

    factory<EvolutionaryIteration<*, *>> {
        GeneticIteration<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(),
        )
    }
    factory {
        SelectSurvivors<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get(), get(), get(),
        )
    }
    factory {
        CrossOvers<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get(), get(), get(), get(),
        )
    }
    factory<CrossOverOperator<*, *>> {
        HeuristicCrossOver<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get(), get(), get(),
        )
    }
    factory<MutateChildren<*, *>> {
        MutateChildrenBySwap<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get(), get(), get(),
        )
    }

    single<IterativeAlgorithmStateWithMultipleCandidates<OnePartRepresentation<Meter>, *>> {
        IterativeAlgorithmStateWithMultipleCandidates()
    }

    single {
        GeneticAlgorithmStatistics<OnePartRepresentation<Meter>, Meter>()
    }

    single {
        BacterialAlgorithmStatistics()
    }
}