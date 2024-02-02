package hu.raven.puppet.logic.step.mutatechildren

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray.Companion.size
import hu.akos.hollo.szabo.collections.slice
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlin.random.Random

data object MutateChildrenBySwap : MutateChildren {

    override fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        if (task.costGraph.objectives.size <= 1)
            return@run

        population.activesAsSequence()
            .filter { it.iterationOfCreation == iteration }
            .shuffled()
            .slice(0..<population.activeCount / 4)
            .forEach { child ->
                val firstCutIndex = Random.nextInt(task.costGraph.objectives.size)
                val secondCutIndex = Random.nextInt(task.costGraph.objectives.size - 1)
                    .let { if (it == firstCutIndex) it + 1 else it }

                child.permutation.swapValues(firstCutIndex, secondCutIndex)

                if (!child.permutation.isFormatCorrect())
                    throw Error("Invalid specimen!")
            }

    }
}