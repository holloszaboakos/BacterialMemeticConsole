package hu.raven.puppet.modules

import hu.raven.puppet.logic.step.initializationofiterative.InitializeByRandom
import hu.raven.puppet.logic.step.initializationofiterative.InitializeLocalSearch
import hu.raven.puppet.logic.step.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.step.initialize.InitializeLocalSearchAlgorithm
import hu.raven.puppet.logic.step.localsearchiteration.LocalSearchIteration
import hu.raven.puppet.logic.step.localsearchiteration.Opt2Iteration
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.TwoPartRepresentation
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithSingleCandidate
import org.koin.core.qualifier.named
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