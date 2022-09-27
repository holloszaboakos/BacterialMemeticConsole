package hu.raven.puppet.modules.dataset

import hu.raven.puppet.logic.specimen.DOnePartRepresentation
import hu.raven.puppet.logic.step.common.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.common.calculatecost.CalculateCostOfCVRPSolutionWithCapacity
import hu.raven.puppet.logic.task.loader.AugeratTaskLoader
import hu.raven.puppet.logic.task.loader.TaskLoader
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.modules.FilePathVariableNames
import org.koin.core.qualifier.named
import org.koin.dsl.module


val augeratDataSetModule = module {
    single(named(AlgorithmParameters.VEHICLE_COUNT)) { 10 }

    single(named(FilePathVariableNames.INPUT_FOLDER)) { "D:\\Git\\GitHub\\SourceCodes\\Kotlin\\JVM\\BacterialMemeticConsole\\input" }
    single(named(FilePathVariableNames.SINGLE_FILE)) { "augerat-1995-set-a\\A-n80-k10.xml" }

    single<TaskLoader> { AugeratTaskLoader() }
    factory<CalculateCost<*>> {
        CalculateCostOfCVRPSolutionWithCapacity<DOnePartRepresentation>()
    }
}