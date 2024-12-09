package hu.raven.puppet.model.solution

import hu.akos.hollo.szabo.math.vector.FloatVector

data class SolutionWithIteration<R>(
    override var iterationOfCreation: Int,
    override var cost: FloatVector?,
    override var representation: R,
) : AlgorithmSolution<R, SolutionWithIteration<R>>, HasIteration {
    override fun clone(): SolutionWithIteration<R> = this.copy()
}
