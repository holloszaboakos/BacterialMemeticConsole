package hu.raven.puppet.model.task

import hu.raven.puppet.model.utility.math.CompleteGraph

data class TspTask(
    val distanceMatrix: CompleteGraph<Unit, Int>
) : AlgorithmTask