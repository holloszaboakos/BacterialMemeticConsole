package hu.raven.puppet.logic.step.evolutionary.genetic.crossoveroperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.physics.PhysicsUnit
import kotlin.random.Random.Default.nextInt

class MaximalPreservationCrossOver<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : CrossOverOperator<S, C>() {

    override fun invoke(
        parents: Pair<S, S>,
        child: S,
    ) {
        val size = child.permutationSize / 4 + nextInt(child.permutationSize / 4)
        val start = nextInt(child.permutationSize - size)
        val seconderCopy = parents.second.copyOfPermutationBy(::MutableList) as MutableList
        val seconderInverse = parents.second.inverseOfPermutation()

        child.setEach { index, _ ->
            if (index < size) {
                seconderCopy[seconderInverse[parents.first[index + start]]] = child.permutationSize
                parents.first[index + start]
            } else
                child.permutationSize
        }
        seconderCopy.removeIf { it == child.permutationSize }

        seconderCopy.forEachIndexed { index, value ->
            child[size + index] = value
        }

        child.iteration = algorithmState.iteration
        child.costCalculated = false
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen!")

    }
}