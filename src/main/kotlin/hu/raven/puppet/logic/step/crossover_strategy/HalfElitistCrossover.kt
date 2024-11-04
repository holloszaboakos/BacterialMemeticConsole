package hu.raven.puppet.logic.step.crossover_strategy

import hu.raven.puppet.logic.operator.crossover_operator.CrossOverOperator
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class HalfElitistCrossover(
    override val crossoverOperators: List<CrossOverOperator>
) : CrossOverStrategy() {

    override operator fun invoke(state: EvolutionaryAlgorithmState<*>): Unit = state.run {
        val children = population.inactivesAsSequence()
            .chunked(2)
            .toList()
        val parent = population.activesAsSequence()
            .shuffled()
            .chunked(2)

        parent.forEachIndexed { index, parentPair ->
            crossoverOperators.first()(
                Pair(
                    parentPair[0].value.permutation,
                    parentPair[1].value.permutation
                ),
                children[index][0].value.permutation
            )
            crossoverOperators.first()(
                Pair(
                    parentPair[1].value.permutation,
                    parentPair[0].value.permutation
                ),
                children[index][1].value.permutation

            )
            children[index][0].let {
                it.value.iterationOfCreation = state.iteration
                it.value.cost = null
                if (!it.value.permutation.isFormatCorrect())
                    throw Error("Invalid specimen!")
            }
            children[index][1].let {
                it.value.iterationOfCreation = state.iteration
                it.value.cost = null
                if (!it.value.permutation.isFormatCorrect())
                    throw Error("Invalid specimen!")
            }
        }
        population.activateAll()
    }
}