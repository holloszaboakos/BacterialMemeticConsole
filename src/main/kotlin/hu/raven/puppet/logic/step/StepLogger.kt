package hu.raven.puppet.logic.step

import hu.raven.puppet.logic.logging.LoggingChannel
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class StepLogger<in T : EvolutionaryAlgorithmState<*>, in M : EvolutionaryAlgorithmState<*>>(
    private val evolutionaryAlgorithmStep: EvolutionaryAlgorithmStep<T>,
    private val loggingChannel: LoggingChannel<M>,
    private val stateToSerializable: (T) -> M
) : EvolutionaryAlgorithmStep<T> {
    init {
        loggingChannel.initialize()
    }

    override fun invoke(state: T) {
        evolutionaryAlgorithmStep(state)
        loggingChannel.send(state.let(stateToSerializable))
    }
}