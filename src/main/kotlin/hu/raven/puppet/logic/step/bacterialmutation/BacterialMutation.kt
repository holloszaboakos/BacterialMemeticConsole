package hu.raven.puppet.logic.step.bacterialmutation

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.bacterialmutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.model.physics.PhysicsUnit

sealed class BacterialMutation<C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<C> {
    protected abstract val mutationOnSpecimen: MutationOnSpecimen<C>
}