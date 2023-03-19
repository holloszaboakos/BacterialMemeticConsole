package hu.raven.puppet.utility.extention

import hu.raven.puppet.model.task.CostGraph
import hu.raven.puppet.model.task.CostGraphEdge

fun CostGraph.getEdgeBetween(from: Int, to: Int): CostGraphEdge {
    return edgesBetween[from][if (to > from) to - 1 else to]
}

fun CostGraph.setEdgeBetween(from: Int, to: Int, edge: CostGraphEdge) {
    edgesBetween[from][if (to > from) to - 1 else to] = edge
}