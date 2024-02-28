package hu.raven.puppet.logic.step.virus_transcription

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.VirusEvolutionaryAlgorithmState

sealed class Transcription<T> : EvolutionaryAlgorithmStep<VirusEvolutionaryAlgorithmState<T>> {
    protected abstract val virusInfectionRate: Float
    protected abstract val lifeReductionRate: Float
    protected abstract val lifeCoefficient: Float
}