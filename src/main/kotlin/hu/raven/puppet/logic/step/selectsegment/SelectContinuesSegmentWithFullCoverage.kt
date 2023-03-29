package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates

class SelectContinuesSegmentWithFullCoverage<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val taskHolder: VRPTaskHolder,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val sizeOfPopulation: Int,
    override val iterationLimit: Int,
    override val geneCount: Int,
    override val cloneSegmentLength: Int
) :
    SelectSegment<S, C>() {
    private val randomizer: IntArray by lazy {
        (0 until cloneSegmentLength)
            .shuffled()
            .toIntArray()
    }

    override fun invoke(
        specimen: S,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment = algorithmState.run {
        val segmentPosition = randomizer[iteration % randomizer.size] + cycleIndex * cloneSegmentLength
        val selectedPositions = IntArray(cloneSegmentLength) { segmentPosition + it }
        val selectedElements = selectedPositions
            .map { specimen[it] }
            .toIntArray()
        Segment(
            selectedPositions,
            selectedElements
        )
    }
}