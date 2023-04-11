package hu.raven.puppet.model.solution

import hu.raven.puppet.model.math.Permutation

class OnePartRepresentationWithIteration<C : Comparable<C>>(
    override var iterationOfCreation: Int,
    override var cost: C?,
    override val objectiveCount: Int,
    override val permutation: Permutation,
) : IterationProduct, SolutionOfOptimization<C>, OnePartRepresentation()