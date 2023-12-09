package hu.raven.puppet.model.task

import hu.akos.hollo.szabo.physics.*

data class TransportUnit(
    val workTimePerDay: Second = Second(0),
    val volumeCapacity: CubicMeter = CubicMeter(0),
    val weightCapacity: Gram = Gram(0),
    val vehicleSpeed: MeterPerSecond = MeterPerSecond(0),
    val salary: EuroPerSecond = EuroPerSecond(0),
    val fuelConsumption: LiterPerMeter = LiterPerMeter(0),
    val fuelPrice: EuroPerLiter = EuroPerLiter(0),
    val basePrice: Euro = Euro(0)
)

