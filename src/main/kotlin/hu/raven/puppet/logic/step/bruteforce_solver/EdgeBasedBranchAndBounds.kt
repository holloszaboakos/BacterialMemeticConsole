package hu.raven.puppet.logic.step.bruteforce_solver

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.matrix.IntMatrix
import hu.raven.puppet.model.utility.math.GraphEdge

fun edgeBasedBranchAndBounds(graph: IntMatrix): Pair<Permutation, Int> {
    val edges = graph.indices[0].flatMap { columnIndex ->
        graph[1].map { rowIndex ->
            GraphEdge(
                sourceNodeIndex = columnIndex,
                targetNodeIndex = rowIndex,
                value = graph[columnIndex, rowIndex]
            )
        }
    }

    edges
        .sortedBy { it.value }
        .forEach {

        }
    TODO("")
}

fun recursiveIteration(
    cost: Int,
    graph: IntMatrix,
    remainingEdges: Array<GraphEdge<Int>>,
    sequences: Array<GraphEdge<Unit>>
): Pair<Int, Set<GraphEdge<Int>>> {

    if (remainingEdges.isEmpty()) {
        return Pair(
            cost + graph[sequences[0].targetNodeIndex, sequences[0].sourceNodeIndex],
            setOf(
                GraphEdge(
                    sourceNodeIndex = sequences[0].targetNodeIndex,
                    targetNodeIndex = sequences[0].sourceNodeIndex,
                    value = graph[sequences[0].targetNodeIndex, sequences[0].sourceNodeIndex]
                )
            )
        )
    }

    remainingEdges.forEach { edge ->
        recursiveIteration(
            cost + edge.value,
            graph,
            remainingEdges
                .filter { it.sourceNodeIndex == edge.sourceNodeIndex || it.targetNodeIndex == edge.targetNodeIndex }
                .toTypedArray(),
            TODO("")
        )
    }

    return TODO("")
}