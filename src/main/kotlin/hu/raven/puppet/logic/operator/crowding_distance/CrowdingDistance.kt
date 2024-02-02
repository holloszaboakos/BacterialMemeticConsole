package hu.raven.puppet.logic.operator.crowding_distance

import hu.akos.hollo.szabo.math.vector.FloatVector

sealed interface CrowdingDistance {
    operator fun invoke(costVectors: List<FloatVector>): FloatVector
}