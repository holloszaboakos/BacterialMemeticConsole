package hu.raven.puppet.logic.step.mutatechildren

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.utility.extention.slice
import kotlin.random.Random

class MutateChildrenBySwap<C : PhysicsUnit<C>>(
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<C>,
) : MutateChildren<C>() {

    override fun invoke(): Unit = algorithmState.run {
        if (task.costGraph.objectives.size > 1)
            algorithmState.population.asSequence()
                .filter { it.iteration == algorithmState.iteration }
                .shuffled()
                .slice(0 until algorithmState.population.size / 4)
                .forEach { child ->
                    val firstCutIndex = Random.nextInt(task.costGraph.objectives.size)
                    val secondCutIndex = Random.nextInt(task.costGraph.objectives.size)
                        .let {
                            if (it == firstCutIndex)
                                (it + 1) % task.costGraph.objectives.size
                            else
                                it
                        }

                    val tmp = child[firstCutIndex]
                    child[firstCutIndex] = child[secondCutIndex]
                    child[secondCutIndex] = tmp
                    if (!child.checkFormat())
                        throw Error("Invalid specimen!")
                }

    }
}