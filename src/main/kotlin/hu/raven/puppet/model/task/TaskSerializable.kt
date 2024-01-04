package hu.raven.puppet.model.task

import hu.akos.hollo.szabo.collections.asImmutable

data class TaskSerializable(
    val transportUnits: List<TransportUnit> = listOf(),
    val costGraph: CostGraphSerializable = CostGraphSerializable()
) {
    constructor(task: Task) : this(
        transportUnits = task.transportUnits.asList(),
        costGraph = CostGraphSerializable(task.costGraph)
    )

    fun toTask() = Task(
        transportUnits = transportUnits.toTypedArray().asImmutable(),
        costGraph = costGraph.toCostGraph(),
    )
}