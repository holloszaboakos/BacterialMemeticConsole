package hu.raven.puppet.logic.operator.initialize_virus_population

import hu.raven.puppet.model.solution.partial.VirusSpecimen

sealed interface InitializeVirusPopulation<R> {
    operator fun invoke(permutations: List<R>): List<VirusSpecimen>

}