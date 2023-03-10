package hu.raven.puppet.logic.step

import hu.raven.puppet.logic.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.utility.inject


abstract class EvolutionaryAlgorithmStep<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : AlgorithmStep<S, C>() {
    protected val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C> by inject()

    protected val sizeOfPopulation: Int by inject(AlgorithmParameters.SIZE_OF_POPULATION)
    protected val iterationLimit: Int by inject(AlgorithmParameters.ITERATION_LIMIT)
    protected val geneCount by lazy { algorithmState.population.first().permutationSize }
}