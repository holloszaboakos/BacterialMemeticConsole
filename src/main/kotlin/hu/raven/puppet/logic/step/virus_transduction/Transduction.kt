package hu.raven.puppet.logic.step.virus_transduction

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.VirusEvolutionaryAlgorithmState

sealed interface Transduction : EvolutionaryAlgorithmStep<VirusEvolutionaryAlgorithmState<*>>