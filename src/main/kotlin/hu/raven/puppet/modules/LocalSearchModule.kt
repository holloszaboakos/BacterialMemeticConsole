package hu.raven.puppet.modules

import org.koin.dsl.module

val localSearchModule = module {
    /*
    factory<InitializeAlgorithm<*, *>>(named("default")) {
        InitializeLocalSearchAlgorithm<TwoPartRepresentation<Meter>, Meter>(
            get(), get(), get(), get(),
        )
    }

    factory<InitializeLocalSearch<*, *>> {
        InitializeByRandom<TwoPartRepresentation<Meter>, Meter>(
            get(), get(), get(),
        )
    }

    factory<LocalSearchIteration<*, *>> {
        Opt2Iteration<TwoPartRepresentation<Meter>, Meter>(get(), get(), get())
    }

    single<IterativeAlgorithmStateWithSingleCandidate<OnePartRepresentation<Meter>, Meter>> {
        IterativeAlgorithmStateWithSingleCandidate(get())
    }

     */
}