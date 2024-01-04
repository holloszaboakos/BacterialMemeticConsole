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
        objectives = objectives.toTypedArray().asImmutable(),
        edgesBetween = edgesBetween.map { it.toTypedArray().asImmutable() }.toTypedArray().asImmutable(),
        edgesFromCenter = edgesFromCenter.toTypedArray().asImmutable(),
        edgesToCenter = edgesToCenter.toTypedArray().asImmutable(),
    )
}