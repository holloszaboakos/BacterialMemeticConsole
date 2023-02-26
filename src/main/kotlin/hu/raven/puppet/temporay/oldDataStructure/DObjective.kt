package hu.raven.puppet.temporay.oldDataStructure

import hu.raven.puppet.model.task.graph.DGps
import java.util.*

data class DObjective(
    var id: String = UUID.randomUUID().toString(),
    val name: String = "",
    var orderInOwner: Int = 0,
    val location: DGps = DGps(),
    val time_Second: Long = 0L,
    val volume_Stere: Long = 0L,
    val weight_Gramm: Long = 0L
)

