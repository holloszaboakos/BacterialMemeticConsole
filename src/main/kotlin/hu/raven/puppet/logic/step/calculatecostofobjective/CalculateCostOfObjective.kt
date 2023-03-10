package hu.raven.puppet.logic.step.calculatecostofobjective

import hu.raven.puppet.model.task.DSalesman
import hu.raven.puppet.model.task.graph.DObjective

class CalculateCostOfObjective {
    operator fun invoke(objective: DObjective, salesman: DSalesman) = 0
    //salesman.payment_EuroPerSecond * objective.time_Second
}