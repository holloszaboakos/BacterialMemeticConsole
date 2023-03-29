package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.utility.extention.asPermutation


class RandomShuffleOfContinuesSegment<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val taskHolder: VRPTaskHolder,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val sizeOfPopulation: Int,
    override val iterationLimit: Int,
    override val geneCount: Int,
    override val cloneSegmentLength: Int,
    override val statistics: BacterialAlgorithmStatistics
) :
    BacterialMutationOperator<S, C>() {
    override fun invoke(
        clone: S,
        selectedSegment: Segment
    ) {
        selectedSegment.positions
            .asPermutation()
            .shuffled()
            .forEachIndexed { readIndex, writeIndex ->
                clone[writeIndex] = selectedSegment.values[readIndex]
            }
    }
}