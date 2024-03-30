package hu.raven.puppet.logic.operator.calculate_cost

import hu.akos.hollo.szabo.math.vector.FloatVector
import hu.raven.puppet.logic.logging.LoggingChannel
import hu.raven.puppet.model.solution.OnePartRepresentation

class CalculateCostWithLogging<T, O : OnePartRepresentation>(
    val classOfSolutionRepresentation: Class<O>,
    val calculateCost: CalculateCost<T>,
    private val loggingChannel: LoggingChannel<Pair<O, List<Float>>>,
    override val task: T
) : CalculateCost<T>() {
    init {
        loggingChannel.initialize()
    }

    override fun invoke(solution: OnePartRepresentation): FloatVector {
        val result = calculateCost(solution)
        if (solution::class.java == classOfSolutionRepresentation) {
            loggingChannel.send(Pair(classOfSolutionRepresentation.cast(solution), result))
        } else {
            throw Exception("Type mismatch!")
        }
        return result
    }

}