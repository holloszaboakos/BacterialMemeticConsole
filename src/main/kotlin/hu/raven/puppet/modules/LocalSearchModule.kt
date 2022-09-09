package hu.raven.puppet.modules

import hu.raven.puppet.logic.specimen.DOnePartRepresentation
import hu.raven.puppet.logic.specimen.DTwoPartRepresentation
import hu.raven.puppet.logic.state.IterativeAlgorithmState
import hu.raven.puppet.logic.step.common.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.step.common.initialize.InitializeLocalSearchAlgorithm
import hu.raven.puppet.logic.step.localsearch.initialize.InitializeByRandom
import hu.raven.puppet.logic.step.localsearch.initialize.InitializeLocalSearch
import hu.raven.puppet.logic.step.localsearch.iteration.LocalSearchIteration
import hu.raven.puppet.logic.step.localsearch.iteration.Opt2Iteration
import org.koin.core.qualifier.named
import org.koin.dsl.module

val localSearchModule = module {
    factory<InitializeAlgorithm<*>>(named("default")) {
        InitializeLocalSearchAlgorithm<DTwoPartRepresentation>()
    }

    factory<InitializeLocalSearch<*>> {
        InitializeByRandom<DTwoPartRepresentation>()
    }

    factory<LocalSearchIteration<*>> {
        Opt2Iteration<DTwoPartRepresentation>()
    }

    single<IterativeAlgorithmState<DOnePartRepresentation>> {
        IterativeAlgorithmState()
    }
}