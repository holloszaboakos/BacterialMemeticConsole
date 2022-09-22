package hu.raven.puppet.utility.extention

import hu.raven.puppet.model.task.graph.DEdge
import hu.raven.puppet.model.task.graph.DGraph

fun DGraph.getEdgeBetween(from: Int, to: Int): DEdge {
    return edgesBetween[from]
        .values[if (to > from) to - 1 else to]
}