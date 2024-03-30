package hu.raven.puppet.logic.step.bruteforce_solver

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.asPermutation
import hu.akos.hollo.szabo.math.matrix.IntMatrix
import hu.akos.hollo.szabo.math.matrix.IntMatrix.Companion.asMatrixColumns
import hu.akos.hollo.szabo.math.vector.IntVector
import hu.akos.hollo.szabo.math.vector.IntVector.Companion.set
import hu.akos.hollo.szabo.math.vector.IntVector2D
import hu.raven.puppet.model.utility.math.GraphEdge
import kotlin.math.max
import kotlin.math.min

fun branchAndBounds(graph: IntMatrix): Pair<Permutation, Int> {
    graph.indices[0].forEach { outer ->
        val cheapestEdge = graph.indices[1]
            .filter { inner -> inner != outer }
            .minOf { inner ->
                if (graph[outer, inner] == 28) {
                    println(Pair(outer, inner))
                }
                graph[outer, inner]
            }
        graph.indices[1].forEach { inner ->
            if (inner != outer) {
                graph[outer][inner] -= cheapestEdge
            }
        }
    }

    var bestPath = intArrayOf(0)
    var bestCost = 800_000
    val routNodes = (1..<graph.dimensions.x)
        .map { locationIndex ->
            Node(
                locationIndex,
                null,
                mutableListOf(),
                false,
                graph[0][locationIndex],
                graph[0][locationIndex] + minimalCostSpanningTree(
                    selectSubGraph(
                        graph,
                        (0..<graph.dimensions.x)
                            .filter { it != locationIndex }
                            .toList()
                    )
                ).sumOf { it.value },
                graph[0][locationIndex] + nearestNeighbour(
                    selectSubGraph(
                        graph,
                        (0..<graph.dimensions.x)
                            .filter { it != locationIndex }
                            .toList()
                    )
                ).sumOf { it.value },
                0
            )
        }
        .sortedBy { it.worstCaseCost }

    routNodes.forEach { routNode ->
        println(routNode.locationIndex)
        if (routNode.potentialCost > bestCost) return@forEach
        var node = routNode
        while (true) {
            val path = buildList {
                add(node.locationIndex)
                var parent = node.parent
                while (parent != null) {
                    add(parent.locationIndex)
                    parent = parent.parent
                }
                add(0)
            }
                .reversed()
                .toIntArray()
            node.children
                .addAll(extractChildrenOf(node, graph, path)
                    .filter { it.potentialCost < bestCost })
            node.visited = true
            //LEAF
            node = if (path.size == graph.dimensions.x) {
                println()
                println("LEAF: ${node.locationIndex}")
                if (node.pathCost + graph[node.locationIndex][0] < bestCost) {
                    if (path.toSet().size != path.size)
                        throw Exception("Duplicate in path!")
                    bestPath = path
                    bestCost = node.pathCost + graph[node.locationIndex][0]
                    println("$bestCost ${path.asList()}")
                }
                findNewNode(node, bestCost) ?: break
            } else {
                node.children.retainAll(node.children.filter { it.potentialCost < bestCost })
                if (node.children.isNotEmpty()) {
                    node.children.removeAt(0)
                } else {
                    //println("Out of children ${node.level}  $bestCost ${node.locationIndex} ${node.potentialCost} ${node.pathCost}")
                    findNewNode(node, bestCost) ?: break
                }
            }
        }
        println(bestCost)
    }

    return Pair(bestPath.asPermutation(), bestCost)
}

private fun findNewNode(node: Node, bestCost: Int): Node? {
    var parent = node.parent
    var newNode: Node? = null
    while (parent != null) {
        parent.children.removeIf { it.potentialCost > bestCost || it.visited }
        if (parent.children.size > 0) {
            newNode = parent.children.removeAt(0)
            break
        }
        val newParent = parent.parent
        newParent?.children?.remove(parent)
        parent = parent.parent
    }

    return newNode
}

private fun extractChildrenOf(node: Node, graph: IntMatrix, path: IntArray): MutableList<Node> {
    return (0..<graph.dimensions.x)
        .asSequence()
        .filter { it !in path }
        .map { locationIndex ->
            Node(
                locationIndex,
                node,
                mutableListOf(),
                false,
                node.pathCost +
                        graph[node.locationIndex][locationIndex],
                node.pathCost +
                        graph[node.locationIndex][locationIndex] +
                        minimalCostSpanningTree(
                            selectSubGraph(
                                graph,
                                (0..<graph.dimensions.x)
                                    .filter { it !in path || it == 0 }
                                    .toList()
                            )
                        ).sumOf { it.value },
                node.pathCost +
                        graph[node.locationIndex][locationIndex] +
                        nearestNeighbour(
                            selectSubGraph(
                                graph,
                                (0..<graph.dimensions.x)
                                    .filter { it !in path || it == 0 }
                                    .toList()
                            )
                        ).sumOf { it.value },
                node.level + 1
            )
        }
        .sortedBy { it.worstCaseCost + it.potentialCost }
        .toMutableList()
}

private data class Node(
    val locationIndex: Int,
    val parent: Node?,
    val children: MutableList<Node>,
    var visited: Boolean,
    val pathCost: Int,
    val potentialCost: Int,
    val worstCaseCost: Int,
    val level: Int
)

private fun minimalCostSpanningTree(graph: IntMatrix): Array<GraphEdge<Int>> {
    val nodeGrouping = IntVector(graph.dimensions.x) { it }
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
            if (nodeGrouping[edge.sourceNodeIndex] == nodeGrouping[edge.targetNodeIndex]) {
                false
            } else {
                mergeGroups(
                    nodeGrouping,
                    IntVector2D(nodeGrouping[edge.sourceNodeIndex], nodeGrouping[edge.targetNodeIndex])
                )
                true
            }
        }
        .toList()
        .toTypedArray()
}

private fun maximalCostSpanningTree(graph: IntMatrix): Array<GraphEdge<Int>> {
    val nodeGrouping = IntVector(graph.dimensions.x) { it }
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
            if (nodeGrouping[edge.sourceNodeIndex] == nodeGrouping[edge.targetNodeIndex]) {
                false
            } else {
                mergeGroups(
                    nodeGrouping,
                    IntVector2D(nodeGrouping[edge.sourceNodeIndex], nodeGrouping[edge.targetNodeIndex])
                )
                true
            }
        }
        .toList()
        .toTypedArray()
}

private fun nearestNeighbour(graph: IntMatrix): Array<GraphEdge<Int>> {
    val visited = BooleanArray(graph.dimensions.x) { false }
    val selectedEdges = mutableListOf<GraphEdge<Int>>()
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

private fun mergeGroups(nodeGrouping: IntVector, groups: IntVector2D) {
    val smallerGroup = min(groups.x, groups.y)
    val biggerGroup = max(groups.x, groups.y)
    (smallerGroup..<nodeGrouping.size)
        .asSequence()
        .filter { nodeGrouping[it] == biggerGroup }
        .forEach { nodeGrouping[it] = smallerGroup }
}

private fun selectSubGraph(graph: IntMatrix, selectedNodes: List<Int>): IntMatrix {
    val subGraph = Array(selectedNodes.size) { lineIndex ->
        IntVector(selectedNodes.size) { columnIndex ->
            graph[selectedNodes[lineIndex]][selectedNodes[columnIndex]]
        }
    }.asMatrixColumns()
    return subGraph
}