package hu.raven.puppet.model.solution

import hu.akos.hollo.szabo.math.Permutation


sealed interface OnePartRepresentation {
    val permutation: Permutation
}