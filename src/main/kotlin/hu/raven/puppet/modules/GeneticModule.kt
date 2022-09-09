package hu.raven.puppet.modules

import hu.raven.puppet.logic.specimen.DOnePartRepresentation
import hu.raven.puppet.logic.state.EvolutionaryAlgorithmState
import hu.raven.puppet.logic.statistics.GeneticAlgorithmStatistics
import hu.raven.puppet.logic.step.common.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.step.common.initialize.InitializeGeneticAlgorithm
import hu.raven.puppet.logic.step.evolutionary.common.iteration.EvolutionaryIteration
import hu.raven.puppet.logic.step.evolutionary.common.iteration.GeneticIteration
import hu.raven.puppet.logic.step.evolutionary.genetic.CrossOvers
import hu.raven.puppet.logic.step.evolutionary.genetic.SelectSurvivors
import hu.raven.puppet.logic.step.evolutionary.genetic.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.step.evolutionary.genetic.crossoveroperator.HeuristicCrossOver
import hu.raven.puppet.logic.step.evolutionary.genetic.mutatechildren.MutateChildren
import hu.raven.puppet.logic.step.evolutionary.genetic.mutatechildren.MutateChildrenBySwap
import org.koin.core.qualifier.named
import org.koin.dsl.module

val geneticModule = module {
    single(named(AlgorithmParameters.ITERATION_LIMIT)) { 26000 }
    single(named(AlgorithmParameters.SIZE_OF_POPULATION)) { 8000 }

    factory<InitializeAlgorithm<*>> {
        InitializeGeneticAlgorithm<DOnePartRepresentation>()
    }

    factory<EvolutionaryIteration<*>> {
        GeneticIteration<DOnePartRepresentation>()
    }
    factory {
        SelectSurvivors<DOnePartRepresentation>()
    }
    factory {
        CrossOvers<DOnePartRepresentation>()
    }
    factory<CrossOverOperator<*>> {
        HeuristicCrossOver<DOnePartRepresentation>()
    }
    factory<MutateChildren<*>> {
        MutateChildrenBySwap<DOnePartRepresentation>()
    }

    single<EvolutionaryAlgorithmState<DOnePartRepresentation>> {
        EvolutionaryAlgorithmState()
    }

    single {
        GeneticAlgorithmStatistics<DOnePartRepresentation>()
    }
}