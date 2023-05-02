package hu.raven.puppet.logic.step.bacterialmutation

import hu.raven.puppet.logic.operator.bacterialmutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep

sealed class BacterialMutation : EvolutionaryAlgorithmStep {
    protected abstract val mutationOnSpecimen: MutationOnSpecimen
}