package hu.raven.puppet.logic.localsearch.initialize

import hu.raven.puppet.logic.localsearch.SLocalSearch
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class InitializeByRandom : InitializeLocalSearch {
    override operator fun <S : ISpecimenRepresentation> invoke(
        algorithm: SLocalSearch<S>
    ) = algorithm.run {

        actualInstance = subSolutionFactory.produce(0, Array(salesmen.size) { index ->
            if (index == 0)
                IntArray(costGraph.objectives.size) { it }
            else
                intArrayOf()
        }).apply {
            shuffle()
        }
        calculateCostOf(actualInstance)
    }
}