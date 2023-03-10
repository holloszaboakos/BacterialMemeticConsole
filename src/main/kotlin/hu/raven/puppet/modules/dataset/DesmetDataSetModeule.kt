package hu.raven.puppet.modules.dataset

import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.calculatecost.CalculateCostOfCVRPSolutionWithCapacity
import hu.raven.puppet.logic.task.loader.DesmetTaskLoader
import hu.raven.puppet.logic.task.loader.TaskLoader
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.modules.FilePathVariableNames
import org.koin.core.qualifier.named
import org.koin.dsl.module

val desmetDataSetModule = module {
    single(named(AlgorithmParameters.VEHICLE_COUNT)) { 20 }

    single(named(FilePathVariableNames.INPUT_FOLDER)) { "input" }
    single(named(FilePathVariableNames.SINGLE_FILE)) { "de-smet//belgium-road-time-n1000-k20.vrp" }

    single<TaskLoader> { DesmetTaskLoader() }
    factory<CalculateCost<*, *>> {
        CalculateCostOfCVRPSolutionWithCapacity<OnePartRepresentation<Meter>>()
    }
}