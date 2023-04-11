package hu.raven.puppet.logic.step.crossoverstrategy

import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class HalfElitistCrossover<C : PhysicsUnit<C>>(
    override val crossoverOperators: List<CrossOverOperator<C>>
) : CrossOverStrategy<C>() {

    override operator fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
        val children = population.inactivesAsSequence().chunked(2).toList()
        val parent = population.activesAsSequence()
            .shuffled()
            .chunked(2)

        parent.forEachIndexed { index, parentPair ->
            crossoverOperators.first()(
                Pair(
                    parentPair[0].content.permutation,
                    parentPair[1].content.permutation
                ),
                children[index][0].content.permutation
            )
            crossoverOperators.first()(
                Pair(
                    parentPair[1].content.permutation,
                    parentPair[0].content.permutation
                ),
                children[index][1].content.permutation
            )
            children[index][0].let {
                it.content.iterationOfCreation = state.iteration
                it.content.cost = null
                if (!it.content.permutation.checkFormat())
                    throw Error("Invalid specimen!")
            }
            children[index][1].let {
                it.content.iterationOfCreation = state.iteration
                it.content.cost = null
                if (!it.content.permutation.checkFormat())
                    throw Error("Invalid specimen!")
            }
        }
        population.activateAll()
    }
}