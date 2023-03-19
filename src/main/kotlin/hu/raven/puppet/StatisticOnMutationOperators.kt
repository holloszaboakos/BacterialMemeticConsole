package hu.raven.puppet

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.calculatecost.CalculateCostOfTspSolution
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.logic.task.loader.TspTaskLoader
import hu.raven.puppet.logic.task.loader.TaskLoader
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.factory.OnePartRepresentationFactory
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmState
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithSingleCandidate
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.modules.FilePathVariableNames
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun main() {
    startKoin {
        modules(
            module {
                single<CalculateCost<*, *>> {
                    CalculateCostOfTspSolution<OnePartRepresentation<Meter>>()
                }
                single { BacterialAlgorithmStatistics() }
                single { DoubleLogger() }
                single { VRPTaskHolder() }
                single<SolutionRepresentationFactory<*, *>> {
                    OnePartRepresentationFactory<Meter>()
                }
                single(named(FilePathVariableNames.INPUT_FOLDER)) { Int.MAX_VALUE }
                single(named(AlgorithmParameters.CLONE_SEGMENT_LENGTH)) { Int.MAX_VALUE }
                single(named(AlgorithmParameters.SIZE_OF_POPULATION)) { Int.MAX_VALUE }
                single(named(AlgorithmParameters.ITERATION_LIMIT)) { Int.MAX_VALUE }
                single<TaskLoader> { TspTaskLoader() }
                single<IterativeAlgorithmState> { IterativeAlgorithmStateWithSingleCandidate() }
            }
        )

    }

}