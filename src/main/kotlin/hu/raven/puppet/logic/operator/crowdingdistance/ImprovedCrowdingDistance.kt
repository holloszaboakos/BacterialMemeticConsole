package hu.raven.puppet.logic.operator.crowdingdistance

data object ImprovedCrowdingDistance : CrowdingDistance {
    override fun invoke(costVectors: List<FloatArray>): FloatArray {
        if(costVectors.isEmpty()) return floatArrayOf()

        val dimensions = costVectors[0].size
        if (costVectors.any { it.size != dimensions }) {
            throw Exception("Inconsistent cost dimension!")
        }

        val crowdingDistances = FloatArray(costVectors.size) { 0f }
        for (dimensionIndex in 0 until dimensions) {
            val specimenInOrderByCost = costVectors
                .withIndex()
                .sortedBy { it.value[dimensionIndex] }

            crowdingDistances[specimenInOrderByCost.first().index] = Float.POSITIVE_INFINITY
            crowdingDistances[specimenInOrderByCost.last().index] = Float.POSITIVE_INFINITY
            val minMaxDistance =
                specimenInOrderByCost.last().value[dimensionIndex] -
                        specimenInOrderByCost.first().value[dimensionIndex]

            if(minMaxDistance == 0f) continue

            specimenInOrderByCost
                .withIndex()
                .toList()
                .slice(1 until specimenInOrderByCost.lastIndex)
                .forEach { (indexByCost, indexedSpecimen) ->
                    val specimenWithHigherCost = specimenInOrderByCost[indexByCost + 1]

                    val crowdingDistance = (
                            specimenWithHigherCost.value[dimensionIndex] -
                                    indexedSpecimen.value[dimensionIndex]
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