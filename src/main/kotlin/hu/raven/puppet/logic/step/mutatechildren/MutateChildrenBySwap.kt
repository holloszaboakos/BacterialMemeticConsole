package hu.raven.puppet.logic.step.mutatechildren

import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.slice
import kotlin.random.Random

class MutateChildrenBySwap : MutateChildren() {

    override fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        if (task.costGraph.objectives.size > 1)
            population.activesAsSequence()
                .filter { it.iterationOfCreation == iteration }
                .shuffled()
                .slice(0 until population.activeCount / 4)
                .forEach { child ->
                    val firstCutIndex = Random.nextInt(task.costGraph.objectives.size)
                    val secondCutIndex = Random.nextInt(task.costGraph.objectives.size)
                        .let {
                            if (it == firstCutIndex)
                                (it + 1) % task.costGraph.objectives.size
                            else
                                it
                        }

                    val tmp = child.permutation[firstCutIndex]
                    child.permutation[firstCutIndex] = child.permutation[secondCutIndex]
                    child.permutation[secondCutIndex] = tmp
                    if (!child.permutation.checkFormat())
                        throw Error("Invalid specimen!")
                }

    }
}