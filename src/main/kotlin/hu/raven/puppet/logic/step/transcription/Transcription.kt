package hu.raven.puppet.logic.step.transcription

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.VirusEvolutionaryAlgorithmState

sealed class Transcription : EvolutionaryAlgorithmStep<VirusEvolutionaryAlgorithmState> {
    protected abstract val virusInfectionRate: Float
    protected abstract val lifeReductionRate: Float
    protected abstract val lifeCoefficient: Float
}