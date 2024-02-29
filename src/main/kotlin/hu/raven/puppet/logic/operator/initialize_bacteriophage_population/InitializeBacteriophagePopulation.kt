package hu.raven.puppet.logic.operator.initialize_bacteriophage_population

import hu.raven.puppet.model.solution.BacteriophageSpecimen

sealed interface InitializeBacteriophagePopulation {
    operator fun invoke(): List<BacteriophageSpecimen>

}