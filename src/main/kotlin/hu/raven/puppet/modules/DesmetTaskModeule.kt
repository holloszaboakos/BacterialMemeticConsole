package hu.raven.puppet.modules

import hu.raven.puppet.logic.specimen.DOnePartRepresentation
import hu.raven.puppet.logic.step.common.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.common.calculatecost.CalculateCostOfCVRPSolutionWithCapacity
import hu.raven.puppet.logic.task.loader.DesmetTaskLoader
import hu.raven.puppet.logic.task.loader.TaskLoader
import org.koin.core.qualifier.named
import org.koin.dsl.module

val desmetTaskModule = module {
    single(named(FilePathVariableNames.INPUT_FOLDER)) { "D:\\Git\\GitHub\\SourceCodes\\Kotlin\\JVM\\BacterialMemeticConsole\\input" }
    single(named(FilePathVariableNames.DESMET_FILE)) { "de-smet\\belgium-road-time-n50-k10.vrp" }
    single<TaskLoader> { DesmetTaskLoader() }
    factory<CalculateCost<*>> {
        CalculateCostOfCVRPSolutionWithCapacity<DOnePartRepresentation>()
    }
}