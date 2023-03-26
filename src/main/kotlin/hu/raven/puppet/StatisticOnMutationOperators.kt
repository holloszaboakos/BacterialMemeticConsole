package hu.raven.puppet

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.bacterialmutationoperator.*
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.calculatecost.CalculateCostOfTspSolution
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.logic.task.loader.TaskLoader
import hu.raven.puppet.logic.task.loader.TspTaskLoader
import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.solution.factory.OnePartRepresentationFactory
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.modules.FilePathVariableNames
import hu.raven.puppet.utility.inject
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun main() {
    val objectiveCount = 15
    startKoin {
        modules(
            module {
                single(named(FilePathVariableNames.INPUT_FOLDER)) { "input/tsp" }
                single(named(FilePathVariableNames.OUTPUT_FOLDER)) { "output" }
                single(named(FilePathVariableNames.SINGLE_FILE)) { "size16instance9.json" }
                single(named(AlgorithmParameters.CLONE_SEGMENT_LENGTH)) { objectiveCount }
                single(named(AlgorithmParameters.SIZE_OF_POPULATION)) { 1 }
                single(named(AlgorithmParameters.ITERATION_LIMIT)) { Int.MAX_VALUE }
                single<CalculateCost<*, *>> {
                    CalculateCostOfTspSolution<OnePartRepresentation<Meter>>()
                }
                single { BacterialAlgorithmStatistics() }
                single { DoubleLogger() }
                single { VRPTaskHolder() }
                single<SolutionRepresentationFactory<*, *>> {
                    OnePartRepresentationFactory<Meter>()
                }
                single<TaskLoader> { TspTaskLoader() }
                single<IterativeAlgorithmStateWithMultipleCandidates<*, *>> { IterativeAlgorithmStateWithMultipleCandidates() }
            }
        )
    }

    val taskHolder: VRPTaskHolder by inject()
    val calculateCost: CalculateCost<OnePartRepresentation<Meter>, Meter> by inject()
    val task = taskHolder.task
    task.costGraph.edgesFromCenter.forEach { println(it.length) }
    task.costGraph.edgesToCenter.forEach { println(it.length) }
    task.costGraph.edgesBetween.flatten().forEach { println(it.length) }
    val operator : BacterialMutationOperator<OnePartRepresentation<Meter>,Meter> = EdgeBuilderHeuristicOnContinuousSegment()
    (0 until 1000).map {
        val specimen =
            OnePartRepresentation<Meter>(
                0,
                objectiveCount,
                Permutation((0 until objectiveCount).toList().toIntArray()),
                true,
                null,
                0,
                0
            )
        operator(
            specimen,
            Segment(
                (0 until objectiveCount).toList().toIntArray(),
                (0 until objectiveCount).toList().toIntArray()
            )
        )
        calculateCost(specimen)
        specimen.costOrException()
    }
        .sortedBy { it.value }
        .groupBy { it.value.numerator / 100 }
        .forEach { (value, values) ->
            println(value.toString() + " : " + values.size.toString())
        }
}