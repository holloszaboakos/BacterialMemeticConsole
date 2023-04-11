package hu.raven.puppet.model.solution

import hu.raven.puppet.model.math.Permutation

abstract class OnePartRepresentation {
    abstract val objectiveCount: Int
    abstract val permutation: Permutation
}