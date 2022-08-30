package hu.raven.puppet.logic.modules

import hu.raven.puppet.logic.common.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.common.initialize.InitializeLocalSearchAlgorithm
import hu.raven.puppet.logic.localsearch.Opt2
import hu.raven.puppet.logic.localsearch.SLocalSearch
import hu.raven.puppet.logic.localsearch.initialize.InitializeByRandom
import hu.raven.puppet.logic.localsearch.initialize.InitializeLocalSearch
import hu.raven.puppet.logic.localsearch.iteration.LocalSearchIteration
import hu.raven.puppet.logic.localsearch.iteration.Opt2Iteration
import hu.raven.puppet.logic.specimen.DOnePartRepresentation
import hu.raven.puppet.logic.specimen.DTwoPartRepresentation
import org.koin.core.qualifier.named
import org.koin.dsl.module

val localSearchModule = module {
    factory<InitializeAlgorithm<*>>(named("default")) {
        InitializeLocalSearchAlgorithm<DTwoPartRepresentation>(
            algorithm = get()
        )
    }

    factory<InitializeLocalSearch<*>> {
        InitializeByRandom<DTwoPartRepresentation>(
            algorithm = get()
        )
    }

    factory<LocalSearchIteration<*>> {
        Opt2Iteration<DTwoPartRepresentation>(
            algorithm = get()
        )
    }

    single<SLocalSearch<DOnePartRepresentation>> {
        Opt2()
    }
}