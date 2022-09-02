package hu.raven.puppet.model.logging

data class PopulationData(
    val best: SpecimenData,
    val second: SpecimenData?,
    val third: SpecimenData?,
    val worst: SpecimenData,
    val median: SpecimenData,
    val diversity: Double,
    val geneBalance: Double
)
