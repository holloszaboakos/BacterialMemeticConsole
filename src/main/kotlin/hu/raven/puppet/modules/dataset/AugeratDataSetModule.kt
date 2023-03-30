package hu.raven.puppet.modules.dataset

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.calculatecost.CalculateCostOfCVRPSolutionWithCapacity
import hu.raven.puppet.logic.task.loader.AugeratTaskLoader
import hu.raven.puppet.logic.task.loader.TaskLoader
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.modules.FilePathVariableNames
import org.koin.core.qualifier.named
import org.koin.dsl.module


val augeratDataSetModule = module {
    /*
    single(named(AlgorithmParameters.VEHICLE_COUNT)) { 10 }

    single(named(FilePathVariableNames.INPUT_FOLDER)) { "input" }
    single(named(FilePathVariableNames.SINGLE_FILE)) { "augerat-1995-set-a/A-n80-k10.xml" }

    single<TaskLoader> { AugeratTaskLoader() }
    factory<CalculateCost<*, *>> {
        CalculateCostOfCVRPSolutionWithCapacity<OnePartRepresentation<Meter>>(
            get(), get(), get(), get()
        )
    }

     */
}