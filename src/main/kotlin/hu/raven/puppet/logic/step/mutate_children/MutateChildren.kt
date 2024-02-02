package hu.raven.puppet.logic.step.mutate_children

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

sealed interface MutateChildren : EvolutionaryAlgorithmStep<EvolutionaryAlgorithmState>