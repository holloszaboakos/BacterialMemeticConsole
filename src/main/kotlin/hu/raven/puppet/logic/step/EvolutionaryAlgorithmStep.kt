package hu.raven.puppet.logic.step

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates


abstract class EvolutionaryAlgorithmStep<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : AlgorithmStep<S, C>() {

    protected abstract val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>
    protected abstract val sizeOfPopulation: Int
    protected abstract val iterationLimit: Int
    protected abstract val geneCount: Int
}