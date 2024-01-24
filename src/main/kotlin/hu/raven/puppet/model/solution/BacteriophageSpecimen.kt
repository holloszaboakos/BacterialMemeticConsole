package hu.raven.puppet.model.solution

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray

data class BacteriophageSpecimen(
    override val id: Int,
    var removedEdges: ImmutableArray<SimpleGraphEdge>,
    var addedEdges: ImmutableArray<SimpleGraphEdge>,
    var lifeForce: FloatArray?,
) : HasId<Int>