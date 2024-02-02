package hu.raven.puppet.logic.step.bacterial_mutation

import hu.raven.puppet.logic.operator.bacterial_mutation_on_specimen.MutationOnSpecimen
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

sealed class BacterialMutation : EvolutionaryAlgorithmStep<EvolutionaryAlgorithmState> {
    protected abstract val mutationOnSpecimen: MutationOnSpecimen
}