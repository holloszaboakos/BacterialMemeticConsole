package hu.raven.puppet.model.solution

import hu.akos.hollo.szabo.math.vector.FloatVector

data class SolutionWithIndex<R>(
    override val index: Int,
    override var cost: FloatVector?,
    override var representation: R,
) : AlgorithmSolution<R, SolutionWithIndex<R>>, HasIndex {
    override fun clone(): SolutionWithIndex<R> = this.copy()
}