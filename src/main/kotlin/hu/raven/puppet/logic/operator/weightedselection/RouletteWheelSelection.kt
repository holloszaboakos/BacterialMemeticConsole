package hu.raven.puppet.logic.operator.weightedselection

import kotlin.random.Random

class RouletteWheelSelection<T : Any> {
    operator fun invoke(candidatesWithWeight: Array<Pair<Float, T>>): T {
        val sumOfWeights = candidatesWithWeight.map { it.first }.sum()
        val choice = Random.nextFloat() * sumOfWeights
        var fill = 0f
        val selectedIndex = candidatesWithWeight
            .map { it.first }
            .indexOfFirst {
                fill += it
                fill >= choice
            }
        return candidatesWithWeight[selectedIndex].second
    }
}