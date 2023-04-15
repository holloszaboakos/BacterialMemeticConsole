package hu.raven.puppet.model.solution

import hu.raven.puppet.model.math.Permutation

interface OnePartRepresentation {
    val objectiveCount: Int
    val permutation: Permutation
}