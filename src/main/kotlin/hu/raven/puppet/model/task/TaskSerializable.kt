package hu.raven.puppet.model.task

data class TaskSerializable(
    val transportUnits: List<TransportUnit> = listOf(),
    val costGraph: CostGraphSerializable = CostGraphSerializable()
) {
    constructor(task: Task) : this(
        transportUnits = task.transportUnits.asList(),
        costGraph = CostGraphSerializable(task.costGraph)
    )

    fun toTask() = Task(
        transportUnits = transportUnits.toTypedArray(),
        costGraph = costGraph.toCostGraph(),
    )
}