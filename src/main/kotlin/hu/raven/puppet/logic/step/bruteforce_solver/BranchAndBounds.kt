package hu.raven.puppet.logic.step.bruteforce_solver

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.asPermutation
import hu.akos.hollo.szabo.math.matrix.IntMatrix
import hu.akos.hollo.szabo.math.vector.IntVector
import hu.akos.hollo.szabo.math.vector.IntVector.Companion.set
import hu.akos.hollo.szabo.math.vector.IntVector2D
import hu.raven.puppet.model.utility.math.GraphEdge
import kotlin.math.max
import kotlin.math.min

fun branchAndBounds(graph: IntMatrix): Pair<Permutation, Int> {
    var bestPath = intArrayOf(0)
    var bestCost = Int.MAX_VALUE
    val routNodes = (1..<graph.size)
        .map { locationIndex ->
            Node(
                locationIndex,
                null,
                mutableListOf(),
                false,
                graph[0][locationIndex],
                graph[0][locationIndex] + minimalCostSpanningTree(graph).sumOf { it.value },
                0
            )
        }
        .sortedBy { it.pathCost }

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
            node.children.addAll(extractChildrenOf(node, graph, path).filter { it.potentialCost < bestCost })
            node.visited = true
            //LEAF
            node = if (path.size == graph.size) {
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
                    //println("$bestCost ${node.locationIndex} ${node.potentialCost} ${node.pathCost}")
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
    return (0..<graph.size)
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
                                (0..<graph.size)
                                    .filter { it !in path || it == 0 }
                                    .toList()
                            )
                        ).sumOf { it.value },
                node.level + 1
            )
        }
        .sortedBy { it.potentialCost }
        .toMutableList()
}

private data class Node(
    val locationIndex: Int,
    val parent: Node?,
    val children: MutableList<Node>,
    var visited: Boolean,
    val pathCost: Int,
    val potentialCost: Int,
    val level: Int
)

private fun minimalCostSpanningTree(graph: IntMatrix): Array<GraphEdge<Int>> {
    val nodeGrouping = IntVector(graph.size) { it }
    return (graph.indices)
        .map { from ->
            graph.indices.map { to ->
                GraphEdge<Int>(from, to, graph[from][to])
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
        .toTypedArray()
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
    val subGraph = IntMatrix(selectedNodes.size) { lineIndex ->
        IntVector(selectedNodes.size) { columnIndex ->
            graph[selectedNodes[lineIndex]][selectedNodes[columnIndex]]
        }
    }
    return subGraph
}