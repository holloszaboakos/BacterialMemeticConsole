package hu.raven.puppet.logic.localsearch.initialize

import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.localsearch.SLocalSearch
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import org.koin.java.KoinJavaComponent.inject

class InitializeByRandom<S : ISpecimenRepresentation>(
    override val algorithm: SLocalSearch<S>
) : InitializeLocalSearch<S> {
    val calculateCostOf: CalculateCost<S> by inject(CalculateCost::class.java)

    override operator fun invoke() = algorithm.run {

        actualInstance = subSolutionFactory.produce(0, Array(task.salesmen.size) { index ->
            if (index == 0)
                IntArray(task.costGraph.objectives.size) { it }
            else
                intArrayOf()
        }).apply {
            shuffle()
        }
        calculateCostOf(actualInstance)
    }
}