package hu.raven.puppet.logic.operator.calculate_cost

import hu.akos.hollo.szabo.math.vector.FloatVector
import hu.raven.puppet.model.task.AlgorithmTask

sealed class CalculateCost<R, T : AlgorithmTask> {
    protected abstract val task: T

    abstract operator fun invoke(representation: R): FloatVector
}