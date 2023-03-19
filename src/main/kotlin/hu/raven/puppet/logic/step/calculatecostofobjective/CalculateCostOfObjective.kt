package hu.raven.puppet.logic.step.calculatecostofobjective

import hu.raven.puppet.model.task.CostGraphVertex
import hu.raven.puppet.model.task.TransportUnit

class CalculateCostOfObjective {
    operator fun invoke(objective: CostGraphVertex, salesman: TransportUnit) = 0
    //salesman.payment_EuroPerSecond * objective.time_Second
}