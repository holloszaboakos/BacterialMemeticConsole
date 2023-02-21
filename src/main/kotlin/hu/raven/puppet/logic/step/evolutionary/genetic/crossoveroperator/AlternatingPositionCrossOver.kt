package hu.raven.puppet.logic.step.evolutionary.genetic.crossoveroperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.physics.PhysicsUnit

class AlternatingPositionCrossOver<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : CrossOverOperator<S, C>() {

    override fun invoke(
        parents: Pair<S, S>,
        child: S,
    ) {
        val parentsL = listOf(parents.first, parents.second)
        val childContains = BooleanArray(child.permutationSize) { false }
        child.setEach { _, _ -> child.permutationSize }

        var counter = 0
        (0 until child.permutationSize).forEach { geneIndex ->
            parentsL.forEach { parent ->
                if (!childContains[parent[geneIndex]]) {
                    child[counter] = parent[geneIndex]
                    childContains[child[counter]] = true
                    counter++
                }
            }
        }

        child.iteration = algorithmState.iteration
        child.costCalculated = false
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen!")

    }
}