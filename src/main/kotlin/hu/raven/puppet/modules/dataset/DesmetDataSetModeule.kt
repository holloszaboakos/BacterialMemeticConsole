package hu.raven.puppet.modules.dataset

import hu.raven.puppet.logic.specimen.DOnePartRepresentation
import hu.raven.puppet.logic.step.common.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.common.calculatecost.CalculateCostOfCVRPSolutionWithCapacity
import hu.raven.puppet.logic.task.loader.DesmetTaskLoader
import hu.raven.puppet.logic.task.loader.TaskLoader
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.modules.FilePathVariableNames
import org.koin.core.qualifier.named
import org.koin.dsl.module

val desmetDataSetModule = module {
    single(named(AlgorithmParameters.VEHICLE_COUNT)) { 20 }

    single(named(FilePathVariableNames.INPUT_FOLDER)) { "D:\\Git\\GitHub\\SourceCodes\\Kotlin\\JVM\\BacterialMemeticConsole\\input" }
    single(named(FilePathVariableNames.SINGLE_FILE)) { "de-smet\\belgium-road-time-n1000-k20.vrp" }

    single<TaskLoader> { DesmetTaskLoader() }
    factory<CalculateCost<*>> {
        CalculateCostOfCVRPSolutionWithCapacity<DOnePartRepresentation>()
    }
}