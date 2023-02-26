package hu.raven.puppet.temporay.oldDataStructure

import java.util.*

data class DSalesman(
    var id: String = UUID.randomUUID().toString(),
    val name: String = "",
    var orderInOwner: Int = 0,
    val workTime_SecondPerDay: Long = 0L,

    val volumeCapacity_Stere: Long = 0L,
    val weightCapacity_Gramm: Long = 0L,

    val vechicleSpeed_MeterPerSecond: Long = 0L,

    val payment_EuroPerSecond: Double = 0.0,

    val fuelConsuption_LiterPerMeter: Double = 0.0,
    val fuelPrice_EuroPerLiter: Double = 0.0,

    val basePrice_Euro: Double = 0.0
)

