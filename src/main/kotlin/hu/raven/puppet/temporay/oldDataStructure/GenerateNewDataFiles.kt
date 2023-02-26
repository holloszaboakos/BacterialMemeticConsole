package hu.raven.puppet.temporay.oldDataStructure

import com.google.gson.Gson
import hu.raven.puppet.model.physics.*
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer

fun main() {
    val gson = Gson()
    val resourceLoader = ResourceLoader()

    val edgesBetweenStream = resourceLoader.loadResource("/input/hungarianExample/between.json")
    val edgesBetween = gson.fromJson(
        edgesBetweenStream.reader(),
        Array<DEdgeArray>::class.java
    )
    val edgesBetweenTransformed = edgesBetween.asSequence()
        .map {
            hu.raven.puppet.model.task.graph.DEdgeArray(
                id = it.id,
                orderInOwner = it.orderInOwner,
                values = it.values.asSequence()
                    .map { edge ->
                        hu.raven.puppet.model.task.graph.DEdge(
                            id = edge.id,
                            name = edge.name,
                            orderInOwner = edge.orderInOwner,
                            length = Meter(edge.length_Meter),
                            route = edge.route
                        )
                    }
                    .toList()
                    .toTypedArray()
            )
        }.toList()
    File("output/edgesBetween.json").outputStream().channel.write(
        ByteBuffer.wrap(gson.toJson(edgesBetweenTransformed).encodeToByteArray())
    )

    val edgesFromCenterStream = resourceLoader.loadResource("/input/hungarianExample/fromCenter.json")
    val edgesFromCenter = gson.fromJson(
        edgesFromCenterStream.reader(),
        Array<DEdge>::class.java
    )
    val edgesFromCenterTransformed = edgesFromCenter.asSequence()
        .map { edge ->
            hu.raven.puppet.model.task.graph.DEdge(
                id = edge.id,
                name = edge.name,
                orderInOwner = edge.orderInOwner,
                length = Meter(edge.length_Meter),
                route = edge.route
            )
        }.toList()
    File("output/edgesFromCenter.json").outputStream().channel.write(
        ByteBuffer.wrap(gson.toJson(edgesFromCenterTransformed).encodeToByteArray())
    )

    val edgesToCenterStream = resourceLoader.loadResource("/input/hungarianExample/toCenter.json")
    val edgesToCenter = gson.fromJson(
        edgesToCenterStream.reader(),
        Array<DEdge>::class.java
    )
    val edgesToCenterTransformed = edgesToCenter.asSequence()
        .map { edge ->
            hu.raven.puppet.model.task.graph.DEdge(
                id = edge.id,
                name = edge.name,
                orderInOwner = edge.orderInOwner,
                length = Meter(edge.length_Meter),
                route = edge.route
            )
        }.toList()
    File("output/edgesToCenter.json").outputStream().channel.write(
        ByteBuffer.wrap(gson.toJson(edgesToCenterTransformed).encodeToByteArray())
    )

    val objectivesStream = resourceLoader.loadResource("/input/hungarianExample/objectives.json")
    val objectives = gson.fromJson(
        objectivesStream.reader(),
        Array<DObjective>::class.java
    )
    val objectivesTransformed = objectives.asSequence()
        .map { objective ->
            hu.raven.puppet.model.task.graph.DObjective(
                id = objective.id,
                name = objective.name,
                orderInOwner = objective.orderInOwner,
                location = objective.location,
                time = Second(objective.time_Second),
                volume = Stere(objective.volume_Stere),
                weight = Gramm(objective.weight_Gramm)
            )
        }.toList()
    File("output/objectives.json").outputStream().channel.write(
        ByteBuffer.wrap(gson.toJson(objectivesTransformed).encodeToByteArray())
    )

    val salesmanStream = resourceLoader.loadResource("/input/hungarianExample/salesman.json")
    val salesman = gson.fromJson(
        salesmanStream.reader(),
        Array<DSalesman>::class.java
    )
    val salesmanTransformed = salesman.asSequence()
        .map { objective ->
            hu.raven.puppet.model.task.DSalesman(
                id = objective.id,
                name = objective.name,
                orderInOwner = objective.orderInOwner,
                workTimePerDay = Second(objective.workTime_SecondPerDay),
                volumeCapacity = Stere(objective.volumeCapacity_Stere),
                weightCapacity = Gramm(objective.weightCapacity_Gramm),
                vehicleSpeed = MeterPerSecond(objective.vechicleSpeed_MeterPerSecond),
                salary = EuroPerSecond((objective.payment_EuroPerSecond * 1000).toLong(), 1000),
                fuelConsumption = LiterPerMeter((objective.fuelConsuption_LiterPerMeter * 1000).toLong(), 1000),
                fuelPrice = EuroPerLiter((objective.fuelPrice_EuroPerLiter * 1000).toLong(), 1000),
                basePrice = Euro((objective.basePrice_Euro * 1000).toLong(), 1000)
            )
        }.toList()
    File("output/salesman.json").outputStream().channel.write(
        ByteBuffer.wrap(gson.toJson(salesmanTransformed).encodeToByteArray())
    )

    println("success")
}

class ResourceLoader {
    fun loadResource(path: String): InputStream {
        return this.javaClass.getResource(path)!!.openStream()
    }
}