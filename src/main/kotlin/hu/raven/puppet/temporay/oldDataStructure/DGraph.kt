package hu.raven.puppet.temporay.oldDataStructure

import hu.raven.puppet.model.task.graph.DGps
import java.util.*

data class DGraph(
    var id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val center: DGps = DGps(),
    val objectives: Array<DObjective> = arrayOf(),
    val edgesBetween: Array<DEdgeArray> = arrayOf(),
    val edgesFromCenter: Array<DEdge> = arrayOf(),
    val edgesToCenter: Array<DEdge> = arrayOf()
) {
    init {
        objectives.forEachIndexed { index, value ->
            value.orderInOwner = index
        }

        edgesBetween.forEachIndexed { indexArray, array ->
            array.orderInOwner = indexArray
            array.values.forEachIndexed { indexValue, value ->
                value.orderInOwner = indexValue
            }
        }
        edgesFromCenter.forEachIndexed { index, value ->
            value.orderInOwner = index
        }
        edgesToCenter.forEachIndexed { index, value ->
            value.orderInOwner = index
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DGraph

        if (id != other.id) return false
        if (name != other.name) return false
        if (center != other.center) return false
        if (!objectives.contentEquals(other.objectives)) return false
        if (!edgesBetween.contentEquals(other.edgesBetween)) return false
        if (!edgesFromCenter.contentEquals(other.edgesFromCenter)) return false
        if (!edgesToCenter.contentEquals(other.edgesToCenter)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + center.hashCode()
        result = 31 * result + objectives.contentHashCode()
        result = 31 * result + edgesBetween.contentHashCode()
        result = 31 * result + edgesFromCenter.contentHashCode()
        result = 31 * result + edgesToCenter.contentHashCode()
        return result
    }
}