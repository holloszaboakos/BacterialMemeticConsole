package hu.raven.puppet.logic.step

import hu.raven.puppet.logic.logging.LoggingChannel
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.state.serializable.EvolutionaryAlgorithmStateForLogging

class StepLogger<R, in S : EvolutionaryAlgorithmState<R>, M : EvolutionaryAlgorithmStateForLogging<R>>(
    private val evolutionaryAlgorithmStep: EvolutionaryAlgorithmStep<R, S>,
    private val loggingChannel: LoggingChannel<M>,
    private val stateToSerializable: (S) -> M
) : EvolutionaryAlgorithmStep<R, S> {
    init {
        loggingChannel.initialize()
    }

    override fun invoke(state: S) {
        evolutionaryAlgorithmStep(state)
        loggingChannel.send(state.let(stateToSerializable))
    }
}