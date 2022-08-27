package hu.raven.puppet.logic.evolutionary

import hu.raven.puppet.logic.evolutionary.setup.BacterialAlgorithmSetup
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import org.koin.java.KoinJavaComponent.inject

class BacterialAlgorithm<S : ISpecimenRepresentation>(
    iterationLimit: Int,
    sizeOfPopulation: Int,
    val cloneCount: Int = 10,
    val cloneSegmentLength: Int = 50,
    val cloneCycleCount: Int = 10, //gene count / clone segment length
    val geneTransferSegmentLength: Int = 300,
    val injectionCount: Int = 30
) : SEvolutionaryAlgorithm<S>(
    iterationLimit,
    sizeOfPopulation
) {
    val geneCount
        get() = population.first().permutationIndices.count()

    override val setup: BacterialAlgorithmSetup by inject(BacterialAlgorithmSetup::class.java)

    suspend fun mutate() = setup.mutate(this)

    fun mutationOperator(
        clone: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ) = setup.mutationOperator(
        this,
        clone,
        selectedPositions,
        selectedElements
    )

    suspend fun geneTransfer() = setup.geneTransfer(this)
    infix fun S.transferGeneTo(to: S) =
        setup.geneTransferOperator(this@BacterialAlgorithm, this, to)

    fun selectSegment(specimen: S) =
        setup.selectSegment(this, specimen, cloneSegmentLength)
}