package hu.raven.puppet.logic.operator.bacteriophage_transduction_operator

import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.model.solution.partial.BacteriophageSpecimen
import hu.raven.puppet.model.utility.math.GraphEdge

class BacteriophageTransductionOperator {
    operator fun invoke(
        beforePermutation: Permutation,
        afterPermutation: Permutation,
        specimenToOverwrite: BacteriophageSpecimen
    ) {
        val differingEdges = (0..beforePermutation.size)
            .map {
                Pair(
                    GraphEdge(it, beforePermutation.after(it), Unit),
                    GraphEdge(it, afterPermutation.after(it), Unit),
                )
            }
            .filter { it.first != it.second }

        specimenToOverwrite.removedEdges = differingEdges
            .map { it.first }
            .toTypedArray()

        specimenToOverwrite.addedEdges = differingEdges
            .map { it.second }
            .toTypedArray()

        specimenToOverwrite.lifeForce = null
    }
}