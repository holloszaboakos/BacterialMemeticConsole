package hu.raven.puppet.modules

import org.koin.dsl.module

val geneticModule = module {
    /*
    single(named(AlgorithmParameters.ITERATION_LIMIT)) { 26000 }
    single(named(AlgorithmParameters.SIZE_OF_POPULATION)) { 10_000 }

    factory<InitializeAlgorithm<*, *>> {
        InitializeGeneticAlgorithm<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get(), get(),
        )
    }

    factory<EvolutionaryIteration<*, *>> {
        GeneticIteration<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(),
        )
    }
    factory {
        SelectSurvivors<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get(), get(),
        )
    }
    factory {
        CrossOvers<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get(), get(), get(),
        )
    }
    factory<CrossOverOperator<*, *>> {
        HeuristicCrossOver<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get(), get(),
        )
    }
    factory<MutateChildren<*, *>> {
        MutateChildrenBySwap<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get(), get(),
        )
    }

    single<IterativeAlgorithmStateWithMultipleCandidates<OnePartRepresentation<Meter>, *>> {
        IterativeAlgorithmStateWithMultipleCandidates(get())
    }

    single {
        GeneticAlgorithmStatistics<OnePartRepresentation<Meter>, Meter>()
    }

    single {
        BacterialAlgorithmStatistics()
    }

     */
}