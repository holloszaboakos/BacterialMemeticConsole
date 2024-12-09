package hu.raven.puppet.logic.operator.initialize_bacteriophage_population

import hu.raven.puppet.model.solution.partial.BacteriophageSpecimen

sealed interface InitializeBacteriophagePopulation {
    operator fun invoke(): List<BacteriophageSpecimen>

}