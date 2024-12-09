package hu.raven.puppet.model.solution

import hu.akos.hollo.szabo.math.vector.FloatVector

interface AlgorithmSolution<R, A : AlgorithmSolution<R, A>> {
    var cost: FloatVector?
    var representation: R
    fun costOrException() = cost ?: throw Exception("Cost of specimen should be already set!")
    fun clone(): A

}