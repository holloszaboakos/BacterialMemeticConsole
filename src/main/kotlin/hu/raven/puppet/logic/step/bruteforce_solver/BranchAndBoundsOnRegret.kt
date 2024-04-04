package hu.raven.puppet.logic.step.bruteforce_solver

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.asPermutation
import hu.akos.hollo.szabo.math.matrix.BooleanMatrix
import hu.akos.hollo.szabo.math.matrix.BooleanMatrix.Companion.asMatrixColumns
import hu.akos.hollo.szabo.math.matrix.IntMatrix
import hu.akos.hollo.szabo.math.vector.BooleanVector
import hu.akos.hollo.szabo.math.vector.IntVector
import hu.akos.hollo.szabo.math.vector.IntVector.Companion.set
import hu.akos.hollo.szabo.math.vector.IntVector2D
import hu.raven.puppet.model.utility.math.GraphEdge
import kotlin.math.max
import kotlin.math.min

fun branchAndBoundsOnRegret(graph: BooleanMatrix): Pair<Permutation, Boolean> {
    var bestPath = intArrayOf(0)
    var bestCost = true
    val routBooleanNodes = (1..<graph.dimensions.x)
        .map { locationIndex ->
            BooleanNode(
                locationIndex = locationIndex,
                parent = null,
                children = mutableListOf(),
                visited = false,
                pathCost = graph[0][locationIndex],
                potentialCost = graph[0][locationIndex] || minimalCostSpanningTree(
                    selectSubGraph(
                        graph,
                        (0..<graph.dimensions.x)
                            .filter { it != locationIndex }
                            .toList()
                    )
                ).any { it.value },
                worstCaseCost = graph[0][locationIndex] || nearestNeighbour(
                    selectSubGraph(
                        graph,
                        (0..<graph.dimensions.x)
                            .filter { it != locationIndex }
                            .toList()
                    )
                ).any { it.value },
                level = 0
            )
        }
        .sortedBy { it.worstCaseCost }

    routBooleanNodes.forEach { routBooleanNode ->
        println("Location index: ${routBooleanNode.locationIndex}")
        if (routBooleanNode.potentialCost) return@forEach
        var BooleanNode = routBooleanNode
        while (true) {
            val path = buildList {
                add(BooleanNode.locationIndex)
                var parent = BooleanNode.parent
                while (parent != null) {
                    add(parent.locationIndex)
                    parent = parent.parent
                }
                add(0)
            }
                .reversed()
                .toIntArray()
            BooleanNode.children
                .addAll(extractChildrenOf(BooleanNode, graph, path)
                    .filter { it.potentialCost < bestCost })
            BooleanNode.visited = true
            //LEAF
            BooleanNode = if (path.size == graph.dimensions.x) {
                println()
                println("LEAF: ${BooleanNode.locationIndex}")
                if (!BooleanNode.pathCost && !graph[BooleanNode.locationIndex][0]) {
                    if (path.toSet().size != path.size)
                        throw Exception("Duplicate in path!")

                    bestPath = path
                    bestCost = false

                    println(path.asList())
                }
                findNewBooleanNode(BooleanNode, bestCost) ?: break
            } else {
                BooleanNode.children.retainAll(BooleanNode.children.filter { !it.potentialCost })
                if (BooleanNode.children.isNotEmpty()) {
                    BooleanNode.children.removeAt(0)
                } else {
                    //println("Out of children ${routBooleanNode.locationIndex} ${BooleanNode.level}  $bestCost ${BooleanNode.locationIndex} ${BooleanNode.potentialCost} ${BooleanNode.pathCost}")
                    findNewBooleanNode(BooleanNode, bestCost) ?: break
                }
            }
        }
        println(bestCost)
    }

    return Pair(bestPath.asPermutation(), bestCost)
}

private fun findNewBooleanNode(BooleanNode: BooleanNode, bestCost: Boolean): BooleanNode? {
    var parent = BooleanNode.parent
    var newBooleanNode: BooleanNode? = null
    while (parent != null) {
        parent.children.removeIf { (it.potentialCost && !bestCost) || it.visited }
        if (parent.children.size > 0) {
            newBooleanNode = parent.children.removeAt(0)
            break
        }
        val newParent = parent.parent
        newParent?.children?.remove(parent)
        parent = parent.parent
    }

    return newBooleanNode
}

private fun extractChildrenOf(
    BooleanNode: BooleanNode,
    graph: BooleanMatrix,
    path: IntArray
): MutableList<BooleanNode> {
    return (0..<graph.dimensions.x)
        .asSequence()
        .filter { it !in path }
        .map { locationIndex ->
            BooleanNode(
                locationIndex,
                BooleanNode,
                mutableListOf(),
                visited = false,
                pathCost = BooleanNode.pathCost ||
                        graph[BooleanNode.locationIndex][locationIndex],
                potentialCost = BooleanNode.pathCost ||
                        graph[BooleanNode.locationIndex][locationIndex] ||
                        minimalCostSpanningTree(
                            selectSubGraph(
                                graph,
                                (0..<graph.dimensions.x)
                                    .filter { it !in path || it == 0 }
                                    .toList()
                            )
                        ).any { it.value },
                worstCaseCost = BooleanNode.pathCost ||
                        graph[BooleanNode.locationIndex][locationIndex] ||
                        nearestNeighbour(
                            selectSubGraph(
                                graph,
                                (0..<graph.dimensions.x)
                                    .filter { it !in path || it == 0 }
                                    .toList()
                            )
                        ).any { it.value },
                level = BooleanNode.level + 1
            )
        }
        .sortedBy { it.worstCaseCost }
        .toMutableList()
}

private data class BooleanNode(
    val locationIndex: Int,
    val parent: BooleanNode?,
    val children: MutableList<BooleanNode>,
    var visited: Boolean,
    val pathCost: Boolean,
    val potentialCost: Boolean,
    val worstCaseCost: Boolean,
    val level: Int
)

private fun minimalCostSpanningTree(graph: BooleanMatrix): Array<GraphEdge<Boolean>> {
    val BooleanNodeGrouping = IntVector(graph.dimensions.x) { it }
    return (graph.indices[0])
        .asSequence()
        .map { from ->
            graph.indices[1]
                .asSequence()
                .map { to ->
                    GraphEdge(from, to, graph[from][to])
                }
        }
        .flatten()
        .sortedBy { it.value }
        .filter { edge ->
            if (BooleanNodeGrouping[edge.sourceNodeIndex] == BooleanNodeGrouping[edge.targetNodeIndex]) {
                false
            } else {
                mergeGroups(
                    BooleanNodeGrouping,
                    IntVector2D(
                        BooleanNodeGrouping[edge.sourceNodeIndex],
                        BooleanNodeGrouping[edge.targetNodeIndex]
                    )
                )
                true
            }
        }
        .toList()
        .toTypedArray()
}

private fun maximalCostSpanningTree(graph: IntMatrix): Array<GraphEdge<Int>> {
    val BooleanNodeGrouping = IntVector(graph.dimensions.x) { it }
    return (graph.indices[0])
        .asSequence()
        .map { from ->
            graph.indices[1]
                .asSequence()
                .map { to ->
                    GraphEdge(from, to, graph[from][to])
                }
        }
        .flatten()
        .sortedByDescending { it.value }
        .filter { edge ->
            if (BooleanNodeGrouping[edge.sourceNodeIndex] == BooleanNodeGrouping[edge.targetNodeIndex]) {
                false
            } else {
                mergeGroups(
                    BooleanNodeGrouping,
                    IntVector2D(
                        BooleanNodeGrouping[edge.sourceNodeIndex],
                        BooleanNodeGrouping[edge.targetNodeIndex]
                    )
                )
                true
            }
        }
        .toList()
        .toTypedArray()
}

private fun nearestNeighbour(graph: BooleanMatrix): Array<GraphEdge<Boolean>> {
    val visited = BooleanArray(graph.dimensions.x) { false }
    val selectedEdges = mutableListOf<GraphEdge<Boolean>>()
    visited[0] = true

    while (visited.any { !it }) {
        val newEdge = if (selectedEdges.isEmpty()) {
            val nearest = graph[0].asSequence()
                .withIndex()
                .filter { !visited[it.index] }
                .minBy { it.value }

            visited[nearest.index] = true

            GraphEdge(
                sourceNodeIndex = 0,
                targetNodeIndex = nearest.index,
                value = nearest.value
            )
        } else {
            val nearest = graph[selectedEdges.last().targetNodeIndex].asSequence()
                .withIndex()
                .filter { !visited[it.index] }
                .minBy { it.value }

            visited[nearest.index] = true

            GraphEdge(
                sourceNodeIndex = selectedEdges.last().targetNodeIndex,
                targetNodeIndex = nearest.index,
                value = nearest.value
            )
        }

        selectedEdges.add(newEdge)
    }

    selectedEdges.add(
        GraphEdge(
            sourceNodeIndex = selectedEdges.last().targetNodeIndex,
            targetNodeIndex = selectedEdges.first().sourceNodeIndex,
            value = graph[selectedEdges.last().targetNodeIndex, selectedEdges.first().sourceNodeIndex]
        )
    )
    return selectedEdges.toTypedArray()
}

private fun mergeGroups(BooleanNodeGrouping: IntVector, groups: IntVector2D) {
    val smallerGroup = min(groups.x, groups.y)
    val biggerGroup = max(groups.x, groups.y)
    (smallerGroup..<BooleanNodeGrouping.size)
        .asSequence()
        .filter { BooleanNodeGrouping[it] == biggerGroup }
        .forEach { BooleanNodeGrouping[it] = smallerGroup }
}

private fun selectSubGraph(graph: BooleanMatrix, selectedBooleanNodes: List<Int>): BooleanMatrix {
    val subGraph = Array(selectedBooleanNodes.size) { lineIndex ->
        BooleanVector(selectedBooleanNodes.size) { columnIndex ->
            graph[selectedBooleanNodes[lineIndex]][selectedBooleanNodes[columnIndex]]
        }
    }.asMatrixColumns()
    return subGraph
}