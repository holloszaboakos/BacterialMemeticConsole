package hu.raven.puppet.logic.step.select_survivers

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

sealed interface SelectSurvivors<R> : EvolutionaryAlgorithmStep<R, EvolutionaryAlgorithmState<R>>