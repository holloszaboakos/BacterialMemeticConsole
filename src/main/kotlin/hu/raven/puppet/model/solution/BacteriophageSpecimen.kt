package hu.raven.puppet.model.solution

import hu.raven.puppet.model.utility.math.GraphEdge

data class BacteriophageSpecimen(
    var removedEdges: Array<GraphEdge<Unit>>,
    var addedEdges: Array<GraphEdge<Unit>>,
    var lifeForce: FloatArray?,
)