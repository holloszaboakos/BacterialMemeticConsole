package hu.raven.puppet.logic.operator.initializeVirusPopulation

import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.model.solution.VirusSpecimen

sealed interface InitializeVirusPopulation {
    operator fun invoke(permutations: List<Permutation>): List<VirusSpecimen>

}