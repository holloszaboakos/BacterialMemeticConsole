package hu.raven.puppet.logic.common.steps.calculatecostofedge

import hu.raven.puppet.model.task.graph.DEdge
import hu.raven.puppet.model.task.DSalesman

class CalculateCostOfEdge {
    operator fun invoke(edge: DEdge, salesman: DSalesman) =
        salesman.fuelPrice_EuroPerLiter * salesman.fuelConsuption_LiterPerMeter * edge.length_Meter +
                salesman.payment_EuroPerSecond * edge.length_Meter / salesman.vechicleSpeed_MeterPerSecond
}