package hu.raven.puppet

import com.google.gson.GsonBuilder
import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray
import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray.Companion.immutableArrayOf
import hu.raven.puppet.model.task.CostGraph
import hu.raven.puppet.model.task.CostGraphEdge
import hu.raven.puppet.model.task.CostGraphVertex
import hu.raven.puppet.model.task.Gps


fun main() {
    val collection: ImmutableArray<CostGraphVertex> = immutableArrayOf()
    val embeddedCollection: ImmutableArray<ImmutableArray<CostGraphEdge>> = immutableArrayOf()
    val gps = Gps()
    val costGraph = CostGraph()
    val text = GsonBuilder().setPrettyPrinting().create().toJson(costGraph)
    println(text)
}