package hu.raven.puppet.logic.operator.crowdingdistance

import hu.akos.hollo.szabo.math.asFloatVector
import hu.akos.hollo.szabo.math.vector.FloatVector
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.set

data object BasicCrowdingDistance : CrowdingDistance {
    override fun invoke(costVectors: List<FloatVector>): FloatVector {
        if (costVectors.isEmpty()) return FloatVector.floatVectorOf()

        val dimensions = costVectors[0].size
        if (costVectors.any { it.size != dimensions }) {
            throw Exception("Inconsistent cost dimension!")
        }

        val crowdingDistances = FloatArray(costVectors.size) { 0f }.asFloatVector()
        for (dimensionIndex in 0 until dimensions) {
            val specimenInOrderByCost = costVectors
                .withIndex()
                .sortedBy { it.value[dimensionIndex] }

            crowdingDistances[specimenInOrderByCost.first().index] = Float.POSITIVE_INFINITY
            crowdingDistances[specimenInOrderByCost.last().index] = Float.POSITIVE_INFINITY
            val minMaxDistance =
                specimenInOrderByCost.last().value[dimensionIndex] -
                        specimenInOrderByCost.first().value[dimensionIndex]

            if (minMaxDistance == 0f) continue

            specimenInOrderByCost
                .withIndex()
                .toList()
                .slice(1 until specimenInOrderByCost.lastIndex)
                .forEach { (indexByCost, indexedSpecimen) ->
                    val specimenWithLowerCost = specimenInOrderByCost[indexByCost - 1]
                    val specimenWithHigherCost = specimenInOrderByCost[indexByCost + 1]

                    val crowdingDistance = (
                            specimenWithHigherCost.value[dimensionIndex] -
                                    specimenWithLowerCost.value[dimensionIndex]
                            ) / minMaxDistance

                    crowdingDistances[indexedSpecimen.index] += crowdingDistance
                }
        }

        crowdingDistances.indices.forEach {
            crowdingDistances[it] = crowdingDistances[it] / dimensions
        }

        return crowdingDistances
    }
}