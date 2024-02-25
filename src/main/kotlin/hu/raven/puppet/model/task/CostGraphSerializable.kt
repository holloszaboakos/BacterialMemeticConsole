package hu.raven.puppet.model.task

import hu.akos.hollo.szabo.collections.asImmutable

data class CostGraphSerializable(
    val center: Gps = Gps(),
    val objectives: List<CostGraphVertex> = listOf(),
    val edgesBetween: List<List<CostGraphEdge>> = listOf(),
    val edgesFromCenter: List<CostGraphEdge> = listOf(),
    val edgesToCenter: List<CostGraphEdge> = listOf()
) {
    constructor(costGraph: CostGraph) : this(
        center = costGraph.center,
        objectives = costGraph.objectives.asList(),
        edgesBetween = costGraph.edgesBetween.map{ it.asList() },
        edgesFromCenter = costGraph.edgesFromCenter.asList(),
        edgesToCenter = costGraph.edgesToCenter.asList(),
    )

    fun toCostGraph() = CostGraph(
        center = center,
        objectives = objectives.toTypedArray(),
        edgesBetween = edgesBetween.map { it.toTypedArray() }.toTypedArray(),
        edgesFromCenter = edgesFromCenter.toTypedArray(),
        edgesToCenter = edgesToCenter.toTypedArray(),
    )
}