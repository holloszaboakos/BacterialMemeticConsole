package hu.raven.puppet.logic.step.calculatecost

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.sumClever

class CalculateCostOfTspSolution<S : SolutionRepresentation<Meter>>(
    override val logger: DoubleLogger,
    override val taskHolder: VRPTaskHolder,
    override val subSolutionFactory: SolutionRepresentationFactory<S, Meter>,
    override val statistics: BacterialAlgorithmStatistics
) : CalculateCost<S, Meter>() {
    override operator fun invoke(specimen: SolutionRepresentation<Meter>) {
        taskHolder.task.run {
            specimen.cost = arrayOf(
                costGraph.edgesFromCenter[specimen[0]].length,
                costGraph.edgesToCenter[specimen[specimen.permutationIndices.last]].length,
                *specimen
                    .map { it }
                    .mapIndexed { index, value ->
                        if (index == 0) {
                            return@mapIndexed Meter(Fraction.new(0))
                        }

                        costGraph.getEdgeBetween(specimen[index - 1], value).length
                    }
                    .toList()
                    .toTypedArray()
            )
                .map(Meter::value)
                .sumClever()
                .let(::Meter)
        }
    }
}