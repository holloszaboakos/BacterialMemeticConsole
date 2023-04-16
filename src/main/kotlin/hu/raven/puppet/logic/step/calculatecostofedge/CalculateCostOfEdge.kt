package hu.raven.puppet.logic.step.calculatecostofedge

import hu.raven.puppet.model.task.CostGraphEdge
import hu.raven.puppet.model.task.TransportUnit

//TODO introduce to cost calculations
class CalculateCostOfEdge {
    operator fun invoke(edge: CostGraphEdge, salesman: TransportUnit) =
        salesman.fuelPrice * salesman.fuelConsumption * edge.length +
                salesman.salary * (edge.length / salesman.vehicleSpeed)
}