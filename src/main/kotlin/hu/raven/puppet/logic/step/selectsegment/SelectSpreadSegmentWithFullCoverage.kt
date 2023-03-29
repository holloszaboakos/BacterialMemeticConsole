package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates

class SelectSpreadSegmentWithFullCoverage<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val taskHolder: VRPTaskHolder,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val sizeOfPopulation: Int,
    override val iterationLimit: Int,
    override val geneCount: Int,
    override val cloneSegmentLength: Int
) : SelectSegment<S, C>() {

    private val randomPermutation: IntArray by lazy {
        IntArray(geneCount) { it }
            .apply { shuffle() }
    }

    override fun invoke(
        specimen: S,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment {
        val segmentStart = cycleIndex * cloneSegmentLength
        val segmentEnd = (cycleIndex + 1) * cloneSegmentLength
        val selectedPositions = randomPermutation
            .slice(segmentStart until segmentEnd)
            .sortedBy { it }
            .toIntArray()
        val selectedElements = selectedPositions
            .map { specimen[it] }
            .toIntArray()
        return Segment(selectedPositions, selectedElements)
    }
}