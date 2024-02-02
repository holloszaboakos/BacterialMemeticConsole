package hu.raven.puppet.logic.operator.initialize_bacteriophage_population

import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.model.solution.BacteriophageSpecimen
import hu.raven.puppet.model.solution.VirusSpecimen

sealed interface InitializeBacteriophagePopulation {
    operator fun invoke(): List<BacteriophageSpecimen>

}