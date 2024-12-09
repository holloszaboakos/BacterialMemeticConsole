package hu.raven.puppet.logic.step.bacteriophage_transcription

import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.BacteriophageAlgorithmState

sealed class BacteriophageTranscription<R> : EvolutionaryAlgorithmStep<R, BacteriophageAlgorithmState<R>> {
    protected abstract val infectionRate: Float
    protected abstract val lifeReductionRate: Float
    protected abstract val lifeCoefficient: Float
    protected abstract val calculateCost: CalculateCost<R, *>
}