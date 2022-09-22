package hu.raven.puppet.modules

import hu.raven.puppet.logic.specimen.DOnePartRepresentation
import hu.raven.puppet.logic.step.common.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.common.calculatecost.CalculateCostOfVRPSolutionWithoutCapacity
import hu.raven.puppet.logic.task.loader.DefaultTaskLoader
import hu.raven.puppet.logic.task.loader.TaskLoader
import hu.raven.puppet.modules.FilePathVariableNames.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val standardTaskModule = module {
    single(named(INPUT_FOLDER)) { "D:\\Git\\GitHub\\SourceCodes\\Kotlin\\JVM\\BacterialMemeticConsole\\input" }
    single(named(GRAPH_FILE)) { "hungarianExample\\graph.json" }
    single(named(EDGES_BETWEEN_FILE)) { "hungarianExample\\between.json" }
    single(named(EDGES_FROM_CENTER_FILE)) { "hungarianExample\\fromCenter.json" }
    single(named(EDGES_TO_CENTER_FILE)) { "hungarianExample\\toCenter.json" }
    single(named(OBJECTIVES_FILE)) { "hungarianExample\\objectives.json" }
    single(named(SALESMAN_FILE)) { "hungarianExample\\salesman.json" }
    single<TaskLoader> { DefaultTaskLoader() }
    factory<CalculateCost<*>> {
        CalculateCostOfVRPSolutionWithoutCapacity<DOnePartRepresentation>()
    }
}