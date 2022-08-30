package hu.raven.puppet.utility

import hu.raven.puppet.model.DGraph

fun DGraph.optimizeGraphByFloyd(objectivesIndices: IntRange) {
    var improvement: Boolean
    for (count in objectivesIndices) {
        println(count)
        improvement = false
        for (fromIndex in objectivesIndices)
            for (toIndex in objectivesIndices) {
                if (fromIndex == toIndex)
                    break

                val routCost =
                    edgesBetween[fromIndex].values[toIndex - if (fromIndex > toIndex) 0 else 1].length_Meter
                for (threwIndex in objectivesIndices) {
                    if (fromIndex == threwIndex || toIndex == threwIndex)
                        continue
                    val routFromCost =
                        edgesBetween[fromIndex].values[threwIndex - if (fromIndex > threwIndex) 0 else 1].length_Meter
                    val routToCost =
                        edgesBetween[threwIndex].values[toIndex - if (threwIndex > toIndex) 0 else 1].length_Meter
                    if (routFromCost.toBigDecimal() + routToCost.toBigDecimal() < routCost.toBigDecimal()) {
                        edgesBetween[fromIndex].values[toIndex - if (fromIndex > toIndex) 0 else 1] =
                            edgesBetween[fromIndex].values[toIndex - if (fromIndex > toIndex) 0 else 1]
                                .copy(length_Meter = routFromCost + routToCost)
                        if (routFromCost + routToCost < 0)
                            println(routFromCost + routToCost)
                        improvement = true
                    }
                }
            }
        if (!improvement) {
            println(improvement)
            break
        }
    }
}