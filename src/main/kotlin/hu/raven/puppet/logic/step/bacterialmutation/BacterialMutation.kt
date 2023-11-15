package hu.raven.puppet.logic.step.bacterialmutation

import hu.raven.puppet.logic.operator.bacterialmutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

sealed class BacterialMutation : EvolutionaryAlgorithmStep<EvolutionaryAlgorithmState> {
    protected abstract val mutationOnSpecimen: MutationOnSpecimen
}