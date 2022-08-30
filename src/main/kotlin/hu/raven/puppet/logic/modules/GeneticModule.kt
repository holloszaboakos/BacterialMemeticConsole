package hu.raven.puppet.logic

import hu.raven.puppet.logic.common.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.common.initialize.InitializeGeneticAlgorithm
import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.evolutionary.common.iteration.EvolutionaryIteration
import hu.raven.puppet.logic.evolutionary.common.iteration.GeneticIteration
import hu.raven.puppet.logic.evolutionary.genetic.CrossOvers
import hu.raven.puppet.logic.evolutionary.genetic.SelectSurvivors
import hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator.HeuristicCrossOver
import hu.raven.puppet.logic.evolutionary.genetic.mutatechildren.MutateChildren
import hu.raven.puppet.logic.evolutionary.genetic.mutatechildren.MutateChildrenBySwap
import hu.raven.puppet.logic.evolutionary.setup.GeneticAlgorithmSetup
import hu.raven.puppet.logic.specimen.DOnePartRepresentation
import org.koin.dsl.module

val geneticModule = module {
    factory<InitializeAlgorithm>() {
        InitializeGeneticAlgorithm()
    }

    factory<EvolutionaryIteration> {
        GeneticIteration()
    }
    factory { SelectSurvivors() }
    factory { CrossOvers() }
    factory<CrossOverOperator> { HeuristicCrossOver() }
    factory<MutateChildren> { MutateChildrenBySwap() }

    factory {
        GeneticAlgorithmSetup(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }

    single<SEvolutionaryAlgorithm<DOnePartRepresentation>> {
        GeneticAlgorithm(
            iterationLimit = 26000,
            sizeOfPopulation = 8000
        )
    }
}