package hu.raven.puppet.logic.operator.calculate_cost

import hu.akos.hollo.szabo.math.vector.FloatVector
import hu.raven.puppet.logic.logging.LoggingChannel
import hu.raven.puppet.model.task.AlgorithmTask

class CalculateCostWithLogging<R : Any, T : AlgorithmTask>(
    val classOfSolutionRepresentation: Class<R>,
    val calculateCost: CalculateCost<R, T>,
    private val loggingChannel: LoggingChannel<Pair<R, List<Float>>>,
    override val task: T
) : CalculateCost<R, T>() {
    init {
        loggingChannel.initialize()
    }

    override fun invoke(representation: R): FloatVector {
        val result = calculateCost(representation)
        if (representation::class.java == classOfSolutionRepresentation) {
            loggingChannel.send(Pair(classOfSolutionRepresentation.cast(representation), result))
        } else {
            throw Exception("Type mismatch!")
        }
        return result
    }

}