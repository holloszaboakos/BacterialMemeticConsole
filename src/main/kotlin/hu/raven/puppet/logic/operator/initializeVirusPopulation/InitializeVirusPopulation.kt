package hu.raven.puppet.logic.operator.initializeVirusPopulation

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.solution.VirusSpecimen

sealed interface InitializeVirusPopulation{
    operator fun invoke(permutations: List<Permutation>): List<VirusSpecimen>

}