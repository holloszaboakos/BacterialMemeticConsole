package hu.raven.puppet.modules

import org.koin.dsl.module

val bacterialModule = module {
    /*
    single(named(ITERATION_LIMIT)) { Int.MAX_VALUE }
    single(named(SIZE_OF_POPULATION)) { 50 }

    single(named(MUTATION_PERCENTAGE)) { 0f }
    single(named(CLONE_COUNT)) { 40 }
    single(named(CLONE_SEGMENT_LENGTH)) { 16 }
    single(named(CLONE_CYCLE_COUNT)) { 5 }

    single(named(GENE_TRANSFER_SEGMENT_LENGTH)) { 900 }
    single(named(INJECTION_COUNT)) { 100 }

    single(named(OPTIMISATION_STEP_LIMIT)) { 1000 }

    single<IterativeAlgorithmStateWithMultipleCandidates<OnePartRepresentation<Meter>, *>> {
        IterativeAlgorithmStateWithMultipleCandidates(get())
    }

    factory<InitializeAlgorithm<*, *>> {
        InitializeBacterialAlgorithm<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get()
        )
    }

    factory<EvolutionaryIteration<*, *>> {
        BacterialIteration<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get(), get(), get(), get(), get(), get(),
        )
    }

    factory<BacterialMutation<*, *>> {
        BacterialMutationOnBestAndLuckyByShuffling<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get(), get(), get(), get(), get(),
        )
    }
    factory<MutationOnSpecimen<*, *>> {
        MutationWithElitistSelection<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(),
        )
    }
    factory<SelectSegment<*, *>> {
        SelectContinuesSegmentWithFullCoverage<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get(), get(), get(),
        )
    }
    factory<BacterialMutationOperator<*, *>> {
        EdgeBuilderHeuristicOnContinuousSegment<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get(), get(), get(), get(),
        )
    }
    factory<hu.raven.puppet.logic.step.genetransfer.GeneTransfer<*, *>> {
        GeneTransferByTournament<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get(), get(), get(), get(), get(),
        )
    }
    factory<GeneTransferOperator<*, *>> {
        GeneTransferByCrossOver<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get(), get(), get(), get(), get(),
        )
    }
    factory<CrossOverOperator<*, *>> {
        HeuristicCrossOver<OnePartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(), get(), get(),
        )
    }

    single {
        BacterialAlgorithmStatistics()
    }

    single {
        CSVLogger()
    }

     */
}
