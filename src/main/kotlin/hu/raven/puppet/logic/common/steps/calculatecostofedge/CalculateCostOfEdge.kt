package hu.raven.puppet.logic.common.steps.calculatecostofedge

import hu.raven.puppet.model.mtsp.DEdge
import hu.raven.puppet.model.mtsp.DSalesman

class CalculateCostOfEdge {
    operator fun invoke(edge: DEdge, salesman: DSalesman) =
        salesman.fuelPrice_EuroPerLiter * salesman.fuelConsuption_LiterPerMeter * edge.length_Meter +
                salesman.payment_EuroPerSecond * edge.length_Meter / salesman.vechicleSpeed_MeterPerSecond
}