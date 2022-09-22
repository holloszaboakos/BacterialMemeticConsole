package hu.raven.puppet.model.desmettask

import hu.raven.puppet.model.desmettask.graph.DistanceMatrix
import hu.raven.puppet.model.desmettask.graph.NodeCoordinate
import hu.raven.puppet.model.desmettask.graph.NodeDemand

data class Task(
    val name: String,
    val comments: Array<String>,
    val type: String,
    val dimension: Int,
    val edgeWeightType: String,
    val edgeWeightFormat: String,
    val edgeWeightUnitOfMeasurement: String,
    val capacity: Int,
    val nodeCoordinates: Array<NodeCoordinate>,
    val distanceMatrix: DistanceMatrix,
    val nodeDemands: Map<Int, NodeDemand>,
    val depotId: Int,

    )