package hu.raven.puppet.logic.common.steps.calculatecostofobjective

import hu.raven.puppet.model.DObjective
import hu.raven.puppet.model.DSalesman

class CalculateCostOfObjective {
    operator fun invoke(objective: DObjective, salesman: DSalesman) =
        salesman.payment_EuroPerSecond * objective.time_Second
}