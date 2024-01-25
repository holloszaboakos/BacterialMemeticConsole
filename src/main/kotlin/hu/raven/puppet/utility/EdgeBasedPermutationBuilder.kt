package hu.raven.puppet.utility

import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.model.utility.SimpleGraphEdge

class EdgeBasedPermutationBuilder(val size: Int) {
    private val sequentialRepresentation = IntArray(size + 1) { -1 }
    private val segmentsOfEdges: MutableList<SimpleGraphEdge> = mutableListOf()
    fun addEdge(edge: SimpleGraphEdge): SimpleGraphEdge {
        sequentialRepresentation[edge.sourceNodeIndex] = edge.targetNodeIndex
        return createNewSegment(edge)
    }

    private fun createNewSegment(selectedEdge: SimpleGraphEdge): SimpleGraphEdge {

        val segmentWithCommonEnd = segmentsOfEdges
            .firstOrNull { it.targetNodeIndex == selectedEdge.sourceNodeIndex }
            ?.also { segmentsOfEdges.remove(it) }

        val segmentWithCommonStart = segmentsOfEdges
            .firstOrNull { it.sourceNodeIndex == selectedEdge.targetNodeIndex }
            ?.also { segmentsOfEdges.remove(it) }

        return when {
            segmentWithCommonStart != null && segmentWithCommonEnd != null ->
                SimpleGraphEdge(
                    segmentWithCommonEnd.sourceNodeIndex,
                    segmentWithCommonStart.targetNodeIndex
                )

            segmentWithCommonStart != null ->
                SimpleGraphEdge(
                    selectedEdge.sourceNodeIndex,
                    segmentWithCommonStart.targetNodeIndex
                )

            segmentWithCommonEnd != null ->
                SimpleGraphEdge(
                    segmentWithCommonEnd.sourceNodeIndex,
                    selectedEdge.targetNodeIndex
                )

            else ->
                selectedEdge

        }
            .also { segmentsOfEdges.add(it) }
    }

    fun build(): Permutation {
        val result = Permutation(size)
        result[0] = sequentialRepresentation.last()
        (1..<result.size).forEach {
            result[it] = sequentialRepresentation[result[it - 1]]
        }
        if (result.checkFormat()) {
            throw Exception("permutation is malformed")
        }
        return result
    }

    fun addLastEdge() {
        if (segmentsOfEdges.size != 1 && !(size == 1 && segmentsOfEdges.isEmpty())) {
            throw Exception("Hamiltonian path is not complete")
        }

        if (sequentialRepresentation.count { it == -1 } > 1) {
            throw Exception("There are more than one missing edges")
        }

        sequentialRepresentation[segmentsOfEdges[0].targetNodeIndex] = segmentsOfEdges[0].sourceNodeIndex
    }
}

fun buildPermutation(size: Int, builderFunction: EdgeBasedPermutationBuilder.() -> Unit): Permutation {
    val builder = EdgeBasedPermutationBuilder(size)
    builder.builderFunction()
    return builder.build()
}