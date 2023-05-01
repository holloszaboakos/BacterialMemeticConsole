package hu.raven.puppet.logic.step.bacterialmutation

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.bacterialmutationonspecimen.MutationOnSpecimen

sealed class BacterialMutation : EvolutionaryAlgorithmStep {
    protected abstract val mutationOnSpecimen: MutationOnSpecimen
}