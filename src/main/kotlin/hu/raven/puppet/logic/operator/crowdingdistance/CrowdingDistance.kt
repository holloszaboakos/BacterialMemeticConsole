package hu.raven.puppet.logic.operator.crowdingdistance

import hu.akos.hollo.szabo.math.vector.FloatVector

sealed interface CrowdingDistance {
    operator fun invoke(costVectors: List<FloatVector>): FloatVector
}