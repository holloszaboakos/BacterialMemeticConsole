package hu.raven.puppet.logic.operator.initialize_bacteriophage_population

import hu.raven.puppet.model.solution.BacteriophageSpecimen

class BasicInitializationOfBacteriophagePopulation(
    private val sizeOfPopulation: Int,
) : InitializeBacteriophagePopulation {
    override fun invoke(): List<BacteriophageSpecimen> =
        (0 until sizeOfPopulation)
            .map {
                BacteriophageSpecimen(
                    arrayOf(),
                    arrayOf(),
                    null
                )
            }
}