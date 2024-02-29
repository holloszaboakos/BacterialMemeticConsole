package hu.raven.puppet.logic.step.mutate_children

import hu.akos.hollo.szabo.collections.slice
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlin.random.Random

data object MutateChildrenBySwap : MutateChildren {

    override fun invoke(state: EvolutionaryAlgorithmState<*>): Unit = state.run {
        if (population.activesAsSequence().first().permutation.size <= 1)
            return@run

        population.activesAsSequence()
            .filter { it.iterationOfCreation == iteration }
            .shuffled()
            .slice(0..<population.activeCount / 4)
            .forEach { child ->
                val firstCutIndex = Random.nextInt(child.permutation.size)
                val secondCutIndex = Random.nextInt(child.permutation.size - 1)
                    .let { if (it == firstCutIndex) it + 1 else it }

                child.permutation.swapValues(firstCutIndex, secondCutIndex)

                if (!child.permutation.isFormatCorrect())
                    throw Error("Invalid specimen!")
            }

    }
}