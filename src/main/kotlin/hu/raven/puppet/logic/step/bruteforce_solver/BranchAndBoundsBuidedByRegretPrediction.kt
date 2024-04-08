package hu.raven.puppet.logic.step.bruteforce_solver

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.asPermutation
import hu.akos.hollo.szabo.math.matrix.DoubleMatrix
import hu.akos.hollo.szabo.math.matrix.IntMatrix
import hu.akos.hollo.szabo.math.matrix.IntMatrix.Companion.asMatrixColumns
import hu.akos.hollo.szabo.math.vector.IntVector
import hu.akos.hollo.szabo.math.vector.IntVector.Companion.set
import hu.akos.hollo.szabo.math.vector.IntVector2D
import hu.raven.puppet.model.utility.math.GraphEdge
import kotlin.math.max
import kotlin.math.min

private data class NodeBasedOnRegret(
    val locationIndex: Int,
    val parent: NodeBasedOnRegret?,
    val children: MutableList<NodeBasedOnRegret>,
    var visited: Boolean,
    val pathCost: Long,
    val predictedRegret: Double,
    val potentialCost: Long,
    val level: Int
)

fun branchAndBoundsGuidedByRegretPrediction(graph: IntMatrix, regret: DoubleMatrix): Pair<Permutation, Long> {

    var bestPath = intArrayOf(0)
    var bestCost = Long.MAX_VALUE
    val routNodes = (1..<graph.dimensions.x)
        .map { locationIndex ->
            NodeBasedOnRegret(
                locationIndex = locationIndex,
                parent = null,
                children = mutableListOf(),
                visited = false,
                pathCost = graph[0][locationIndex].toLong(),
                potentialCost = graph[0][locationIndex].toLong() + minimalCostSpanningTree(
                    selectSubGraph(
                        graph,
                        (0..<graph.dimensions.x)
                            .filter { it != locationIndex }
                            .toList()
                    )
                ).sumOf { it.value.toLong() },
                predictedRegret = regret[0][locationIndex],
                level = 0
            )
        }
        .sortedBy { it.predictedRegret }

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
                .addAll(
                    extractChildrenOf(node, graph, regret, path)
                        .filter { it.potentialCost < bestCost }
                )
            node.visited = true
            //LEAF
            node = if (path.size == graph.dimensions.x) {
                //println()
                //println("LEAF: ${node.locationIndex}")
                if (node.pathCost + graph[node.locationIndex][0] < bestCost) {
                    if (path.toSet().size != path.size)
                        throw Exception("Duplicate in path!")
                    bestPath = path
                    bestCost = node.pathCost + graph[node.locationIndex][0]
                    println("$bestCost ${path.asList()}")
                }
                findNewNode(node, bestCost) ?: break
            } else {
                node.children.removeIf { it.potentialCost > bestCost }
                if (node.children.isNotEmpty()) {
                    node.children.removeAt(0)
                } else {
                    //println("Out of children ${routNode.locationIndex} ${node.level}  $bestCost ${node.locationIndex} ${node.predictedRegret} ${node.pathCost}")
                    findNewNode(node, bestCost) ?: break
                }
            }
        }
        println(bestCost)
    }

    return Pair(bestPath.asPermutation(), bestCost)
}

private fun findNewNode(nodeBasedOnRegret: NodeBasedOnRegret, bestCost: Long): NodeBasedOnRegret? {
    var parent = nodeBasedOnRegret.parent
    var newNodeBasedOnRegret: NodeBasedOnRegret? = null
    while (parent != null) {
        parent.children.removeIf { it.potentialCost > bestCost || it.visited }
        if (parent.children.size > 0) {
            newNodeBasedOnRegret = parent.children.removeAt(0)
            break
        }
        val newParent = parent.parent
        newParent?.children?.remove(parent)
        parent = parent.parent
    }

    return newNodeBasedOnRegret
}

private fun extractChildrenOf(
    nodeBasedOnRegret: NodeBasedOnRegret,
    graph: IntMatrix,
    regret: DoubleMatrix,
    path: IntArray
): MutableList<NodeBasedOnRegret> {
    return (0..<graph.dimensions.x)
        .asSequence()
        .filter { it !in path }
        .map { locationIndex ->
            NodeBasedOnRegret(
                locationIndex = locationIndex,
                parent = nodeBasedOnRegret,
                children = mutableListOf(),
                visited = false,
                pathCost = nodeBasedOnRegret.pathCost +
                        graph[nodeBasedOnRegret.locationIndex][locationIndex],
                potentialCost = nodeBasedOnRegret.pathCost +
                        graph[nodeBasedOnRegret.locationIndex][locationIndex] +
                        minimalCostSpanningTree(
                            selectSubGraph(
                                graph,
                                (0..<graph.dimensions.x)
                                    .filter { it !in path || it == 0 }
                                    .toList()
                            )
                        ).sumOf { it.value },
                predictedRegret = regret[nodeBasedOnRegret.locationIndex][locationIndex],
                level = nodeBasedOnRegret.level + 1
            )
        }
        .sortedBy { it.predictedRegret }
        .toMutableList()
}

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