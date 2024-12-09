package hu.raven.puppet.logic.step.crossover_strategy

import hu.raven.puppet.logic.operator.crossover_operator.CrossOverOperator
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class HalfElitistCrossover<R>(
    override val crossoverOperators: List<CrossOverOperator<R>>
) : CrossOverStrategy<R>() {

    override operator fun invoke(state: EvolutionaryAlgorithmState<R>): Unit = state.run {
        val children = population.inactivesAsSequence()
            .chunked(2)
            .toList()
        val parent = population.activesAsSequence()
            .shuffled()
            .chunked(2)

        parent.forEachIndexed { index, parentPair ->
            crossoverOperators.first()(
                Pair(
                    parentPair[0].value.representation,
                    parentPair[1].value.representation
                ),
                children[index][0].value.representation
            )
            crossoverOperators.first()(
                Pair(
                    parentPair[1].value.representation,
                    parentPair[0].value.representation
                ),
                children[index][1].value.representation

            )
            children[index][0].let {
                it.value.iterationOfCreation = state.iteration
                it.value.cost = null
            }
            children[index][1].let {
                it.value.iterationOfCreation = state.iteration
                it.value.cost = null
            }
        }
        population.activateAll()
    }
}