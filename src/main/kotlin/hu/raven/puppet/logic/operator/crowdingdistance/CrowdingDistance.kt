package hu.raven.puppet.logic.operator.crowdingdistance

sealed interface CrowdingDistance {
    operator fun invoke(costVectors: List<FloatArray>): FloatArray
}