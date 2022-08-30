package hu.raven.puppet.logic.modules

import hu.raven.puppet.logic.AlgorithmManagerService
import org.koin.dsl.module

val commonPostModule = module {
    single {
        AlgorithmManagerService()
    }
}