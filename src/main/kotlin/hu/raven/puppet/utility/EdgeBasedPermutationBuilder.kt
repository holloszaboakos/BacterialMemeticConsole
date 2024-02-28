package hu.raven.puppet.utility

import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.model.utility.math.GraphEdge

class EdgeBasedPermutationBuilder(val size: Int) {
    private val sequentialRepresentation = IntArray(size + 1) { -1 }
    private val segmentsOfEdges: MutableList<GraphEdge<Unit>> = mutableListOf()
    private val availabilityMatrix: Array<BooleanArray> =
        Array(sequentialRepresentation.size) { rowIndex ->
            BooleanArray(sequentialRepresentation.size) { columnIndex -> rowIndex != columnIndex }
        }

    fun isAvailable(edge: GraphEdge<Unit>) = availabilityMatrix[edge.sourceNodeIndex][edge.targetNodeIndex]

    fun addEdge(edge: GraphEdge<Unit>): GraphEdge<Unit> {

        if (!availabilityMatrix[edge.sourceNodeIndex][edge.targetNodeIndex]) {
            throw Exception("Edge is not available!")
        }

        sequentialRepresentation[edge.sourceNodeIndex] = edge.targetNodeIndex
        val newSegment = createNewSegment(edge)

        availabilityMatrix[edge.targetNodeIndex][edge.sourceNodeIndex] = false
        availabilityMatrix[newSegment.targetNodeIndex][newSegment.sourceNodeIndex] = false
        availabilityMatrix.indices.forEach {
            availabilityMatrix[it][edge.targetNodeIndex] = false
            availabilityMatrix[edge.sourceNodeIndex][it] = false
        }

        return newSegment
    }

    fun isComplete() = sequentialRepresentation.none { it == -1 }

    private fun createNewSegment(selectedEdge: GraphEdge<Unit>): GraphEdge<Unit> {

        val segmentWithCommonEnd = segmentsOfEdges
            .firstOrNull { it.targetNodeIndex == selectedEdge.sourceNodeIndex }
            ?.also { segmentsOfEdges.remove(it) }

        val segmentWithCommonStart = segmentsOfEdges
            .firstOrNull { it.sourceNodeIndex == selectedEdge.targetNodeIndex }
            ?.also { segmentsOfEdges.remove(it) }

        return when {
            segmentWithCommonStart != null && segmentWithCommonEnd != null ->
                GraphEdge(
                    segmentWithCommonEnd.sourceNodeIndex,
                    segmentWithCommonStart.targetNodeIndex,
                    Unit
                )

            segmentWithCommonStart != null ->
                GraphEdge(
                    selectedEdge.sourceNodeIndex,
                    segmentWithCommonStart.targetNodeIndex,
                    Unit
                )

            segmentWithCommonEnd != null ->
                GraphEdge(
                    segmentWithCommonEnd.sourceNodeIndex,
                    selectedEdge.targetNodeIndex,
                    Unit
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
        if (!result.isFormatCorrect()) {
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

    fun completeWithRandomAvailableEdges() {
        availabilityMatrix
            .asSequence()
            .flatMapIndexed { sourceIndex, row ->
                row.asSequence()
                    .mapIndexed { targetIndex, available ->
                        Pair(
                            GraphEdge(
                                sourceIndex,
                                targetIndex,
                                Unit
                            ),
                            available
                        )
                    }
            }
            .filter { it.second }
            .map { it.first }
            .shuffled()
            .filter { edge -> isAvailable(edge) }
            .forEach { edge -> addEdge(edge) }
    }

    fun selectRandomFromAvailable(): GraphEdge<Unit> =
        availabilityMatrix
            .mapIndexed { columnIndex, column ->
                column
                    .withIndex()
                    .filter { it.value }
                    .map { (index, _) ->
                        GraphEdge(columnIndex, index, Unit)
                    }
            }
            .flatten()
            .random()
}

fun buildPermutation(size: Int, builderFunction: EdgeBasedPermutationBuilder.() -> Unit): Permutation {
    val builder = EdgeBasedPermutationBuilder(size)
    builder.builderFunction()
    return builder.build()
}