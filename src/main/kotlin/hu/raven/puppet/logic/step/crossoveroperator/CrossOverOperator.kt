package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation

sealed class CrossOverOperator<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<S, C>() {

    abstract operator fun invoke(
        parents: Pair<S, S>,
        child: S
    )
}