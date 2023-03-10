package hu.raven.puppet.logic.step.calculatecostofedge

import hu.raven.puppet.model.task.DSalesman
import hu.raven.puppet.model.task.graph.DEdge

class CalculateCostOfEdge {
    operator fun invoke(edge: DEdge, salesman: DSalesman) = edge.length
    /*salesman.fuelPrice_EuroPerLiter * salesman.fuelConsuption_LiterPerMeter * edge.length_Meter +
            salesman.payment_EuroPerSecond * edge.length_Meter / salesman.vechicleSpeed_MeterPerSecond

     */
}