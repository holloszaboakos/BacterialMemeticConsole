package hu.raven.puppet.logic.step.mutatechildren

import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.extention.slice
import kotlin.random.Random

class MutateChildrenBySwap<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : MutateChildren<S, C>() {

    override fun invoke() {
        if (taskHolder.task.costGraph.objectives.size > 1)
            algorithmState.population.asSequence()
                .filter { it.iteration == algorithmState.iteration }
                .shuffled()
                .slice(0 until algorithmState.population.size / 4)
                .forEach { child ->
                    val firstCutIndex = Random.nextInt(taskHolder.task.costGraph.objectives.size)
                    val secondCutIndex = Random.nextInt(taskHolder.task.costGraph.objectives.size)
                        .let {
                            if (it == firstCutIndex)
                                (it + 1) % taskHolder.task.costGraph.objectives.size
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