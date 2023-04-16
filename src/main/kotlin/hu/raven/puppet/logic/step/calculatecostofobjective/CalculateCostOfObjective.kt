package hu.raven.puppet.logic.step.calculatecostofobjective

import hu.raven.puppet.model.physics.Euro
import hu.raven.puppet.model.task.CostGraphVertex
import hu.raven.puppet.model.task.TransportUnit

//TODO introduce to cost calculations
class CalculateCostOfObjective {
    operator fun invoke(objective: CostGraphVertex, salesman: TransportUnit): Euro = salesman.salary * objective.time
}