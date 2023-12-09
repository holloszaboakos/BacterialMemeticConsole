package hu.raven.puppet.model.solution

import hu.akos.hollo.szabo.math.Permutation


interface OnePartRepresentation {
    val objectiveCount: Int
    val permutation: Permutation
}