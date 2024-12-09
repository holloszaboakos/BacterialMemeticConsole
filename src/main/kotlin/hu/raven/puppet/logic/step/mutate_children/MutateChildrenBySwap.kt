package hu.raven.puppet.logic.step.mutate_children

import hu.akos.hollo.szabo.collections.slice
import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlin.random.Random

data object MutateChildrenBySwap : MutateChildren<Permutation> {

    override fun invoke(state: EvolutionaryAlgorithmState<Permutation>): Unit = state.run {
        if (population.activesAsSequence().first().value.representation.size <= 1)
            return@run

        population.activesAsSequence()
            .filter { it.value.iterationOfCreation == iteration }
            .shuffled()
            .slice(0..<population.activeCount / 4)
            .forEach { child ->
                val firstCutIndex = Random.nextInt(child.value.representation.size)
                val secondCutIndex = Random.nextInt(child.value.representation.size - 1)
                    .let { if (it == firstCutIndex) it + 1 else it }

                child.value.representation.swapValues(firstCutIndex, secondCutIndex)

                if (!child.value.representation.isFormatCorrect())
                    throw Error("Invalid specimen!")
            }

    }
}