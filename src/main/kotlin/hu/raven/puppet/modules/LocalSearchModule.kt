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
import hu.raven.puppet.model.physics.Meter
import org.koin.core.qualifier.named
import org.koin.dsl.module

val localSearchModule = module {
    factory<InitializeAlgorithm<*, *>>(named("default")) {
        InitializeLocalSearchAlgorithm<DTwoPartRepresentation<Meter>, Meter>()
    }

    factory<InitializeLocalSearch<*, *>> {
        InitializeByRandom<DTwoPartRepresentation<Meter>, Meter>()
    }

    factory<LocalSearchIteration<*, *>> {
        Opt2Iteration<DTwoPartRepresentation<Meter>, Meter>()
    }

    single<IterativeAlgorithmState<DOnePartRepresentation<Meter>, Meter>> {
        IterativeAlgorithmState()
    }
}