package hu.raven.puppet.logic.step.selectsurvivers

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.FloatArrayExtensions.vectorLength
import hu.raven.puppet.utility.extention.slice

sealed interface SelectSurvivors : EvolutionaryAlgorithmStep {
}