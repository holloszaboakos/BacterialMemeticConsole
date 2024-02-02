package hu.raven.puppet.logic.step.crossoverstrategy

import hu.raven.puppet.logic.operator.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class HalfElitistCrossover(
    override val crossoverOperators: List<CrossOverOperator>
) : CrossOverStrategy() {

    override operator fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        val children = population.inactivesAsSequence()
            .chunked(2)
            .toList()
        val parent = population.activesAsSequence()
            .shuffled()
            .chunked(2)

        parent.forEachIndexed { index, parentPair ->
            crossoverOperators.first()(
                Pair(
                    parentPair[0].permutation,
                    parentPair[1].permutation
                ),
                children[index][0].permutation
            )
            crossoverOperators.first()(
                Pair(
                    parentPair[1].permutation,
                    parentPair[0].permutation
                ),
                children[index][1].permutation

            )
            children[index][0].let {
                it.iterationOfCreation = state.iteration
                it.cost = null
                if (!it.permutation.isFormatCorrect())
                    throw Error("Invalid specimen!")
            }
            children[index][1].let {
                it.iterationOfCreation = state.iteration
                it.cost = null
                if (!it.permutation.isFormatCorrect())
                    throw Error("Invalid specimen!")
            }
        }
        population.activateAll()
    }
}