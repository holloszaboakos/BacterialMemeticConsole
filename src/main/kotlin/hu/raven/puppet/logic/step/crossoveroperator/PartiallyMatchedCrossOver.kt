package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import kotlin.random.Random

class PartiallyMatchedCrossOver<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val solutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<S, C>
) : CrossOverOperator<S, C>() {

    override fun invoke(
        parents: Pair<S, S>,
        child: S,
    ) {
        val cut = arrayOf(
            Random.nextInt(parents.first.permutationIndices.count()),
            Random.nextInt(parents.first.permutationIndices.count() - 1)
        )
        if (cut[0] == cut[1])
            cut[1]++
        cut.sort()
        val primerParent = parents.first
        val seconderParent = parents.second
        val seconderCopy = seconderParent.copyOfPermutationBy(::MutableList) as MutableList
        val seconderInverse = seconderParent.inverseOfPermutation()

        //copy parent middle to child
        //start mapping
        child.setEach { index, _ ->
            if (index in cut[0]..cut[1])
                child.permutationSize
            else {
                seconderCopy[seconderInverse.value[primerParent[index]]] = child.permutationSize
                primerParent[index]
            }
        }
        seconderCopy.removeIf { it == child.permutationSize }
        //fill empty positions
        seconderCopy.forEachIndexed { index, value ->
            child[cut[0] + index] = value
        }

        child.iteration = algorithmState.iteration
        child.cost = null
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen!")
    }
}