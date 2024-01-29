package hu.raven.puppet.logic.operator.bacteriophagetransduction

import hu.akos.hollo.szabo.collections.asImmutable
import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.model.solution.BacteriophageSpecimen
import hu.raven.puppet.model.utility.SimpleGraphEdge

class BacteriophageTransductionOperator {
    operator fun invoke(
        beforePermutation: Permutation,
        afterPermutation: Permutation,
        specimenToOverwrite: BacteriophageSpecimen
    ) {
        val differingEdges = (0.. beforePermutation.size)
            .map {
                Pair(
                    SimpleGraphEdge(it, beforePermutation.after(it)),
                    SimpleGraphEdge(it, afterPermutation.after(it)),
                )
            }
            .filter { it.first != it.second }

        specimenToOverwrite.removedEdges = differingEdges
            .map { it.first }
            .toTypedArray()
            .asImmutable()

        specimenToOverwrite.addedEdges = differingEdges
            .map { it.second }
            .toTypedArray()
            .asImmutable()

        specimenToOverwrite.lifeForce = null
    }
}