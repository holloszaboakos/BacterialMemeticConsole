package hu.raven.puppet.model.task.desmet

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray
import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray.Companion.contentEquals
import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray.Companion.contentHashCode


data class DesmetTask(
    val name: String,
    val comments: ImmutableArray<String>,
    val type: String,
    val dimension: Int,
    val edgeWeightType: String,
    val edgeWeightFormat: String,
    val edgeWeightUnitOfMeasurement: String,
    val capacity: Int,
    val nodeCoordinates: ImmutableArray<NodeCoordinate>,
    val distanceMatrix: DistanceMatrix,
    val nodeDemands: Map<Int, NodeDemand>,
    val depotId: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DesmetTask

        if (name != other.name) return false
        if (!comments.contentEquals(other.comments)) return false
        if (type != other.type) return false
        if (dimension != other.dimension) return false
        if (edgeWeightType != other.edgeWeightType) return false
        if (edgeWeightFormat != other.edgeWeightFormat) return false
        if (edgeWeightUnitOfMeasurement != other.edgeWeightUnitOfMeasurement) return false
        if (capacity != other.capacity) return false
        if (!nodeCoordinates.contentEquals(other.nodeCoordinates)) return false
        if (distanceMatrix != other.distanceMatrix) return false
        if (nodeDemands != other.nodeDemands) return false
        if (depotId != other.depotId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + comments.contentHashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + dimension
        result = 31 * result + edgeWeightType.hashCode()
        result = 31 * result + edgeWeightFormat.hashCode()
        result = 31 * result + edgeWeightUnitOfMeasurement.hashCode()
        result = 31 * result + capacity
        result = 31 * result + nodeCoordinates.contentHashCode()
        result = 31 * result + distanceMatrix.hashCode()
        result = 31 * result + nodeDemands.hashCode()
        result = 31 * result + depotId
        return result
    }
}