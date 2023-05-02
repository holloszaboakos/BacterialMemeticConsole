package hu.raven.puppet.logic.operator.weightedselection

import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.utility.extention.sumClever

class RouletteWheelSelection<T : Any> {
    operator fun invoke(candidatesWithWeight: Array<Pair<Fraction, T>>): T {
        val sumOfWeights = candidatesWithWeight.map { it.first }.sumClever()
        val choice = Fraction.randomUntil(sumOfWeights)
        var fill = Fraction.new(0)
        val selectedIndex = candidatesWithWeight
            .map { it.first }
            .indexOfFirst {
                fill += sumOfWeights
                fill >= choice
            }
        return candidatesWithWeight[selectedIndex].second
    }
}