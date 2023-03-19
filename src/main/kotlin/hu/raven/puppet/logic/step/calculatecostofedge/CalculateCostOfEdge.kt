package hu.raven.puppet.logic.step.calculatecostofedge

import hu.raven.puppet.model.task.CostGraphEdge
import hu.raven.puppet.model.task.TransportUnit

class CalculateCostOfEdge {
    operator fun invoke(edge: CostGraphEdge, salesman: TransportUnit) = edge.length
    /*salesman.fuelPrice_EuroPerLiter * salesman.fuelConsuption_LiterPerMeter * edge.length_Meter +
            salesman.payment_EuroPerSecond * edge.length_Meter / salesman.vechicleSpeed_MeterPerSecond

     */
}