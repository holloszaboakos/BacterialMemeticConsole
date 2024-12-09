package hu.raven.puppet.job

import com.google.gson.GsonBuilder
import hu.raven.puppet.parsing.model.AlgorithmPhase
import hu.raven.puppet.parsing.model.ConfigurationData
import hu.raven.puppet.parsing.model.SpecimenData
import hu.raven.puppet.parsing.model.StateData
import hu.raven.puppet.parsing.serialization.StateSerializer
import java.io.File
import java.io.FileOutputStream
import java.util.zip.GZIPOutputStream

fun main() {
    val configurationData = ConfigurationData(
        specimenCount = 1000,
        iterationLimit = 2000,
        selectionPercentage = 0.1f,
    )

    val state = StateData(
        phase = AlgorithmPhase.MUTATION,
        generationCount = 100,
        specimens = Array(1000) {
            SpecimenData(
                it,
                permutation = IntArray(1000) { it }.apply { shuffle() }
            )
        }
    )


    val stateSerializer = StateSerializer()
    File("output/log5.gz")
        .let(::FileOutputStream)
        .let(::GZIPOutputStream)
        .use { logFileOut ->
            logFileOut.write(stateSerializer.serialize(state))
        }

    val gson = GsonBuilder().setPrettyPrinting().create()
    File("output/log6.gz")
        .let(::FileOutputStream)
        .let(::GZIPOutputStream)
        .use { logFileOut ->
            logFileOut.write(gson.toJson(state).toByteArray(Charsets.UTF_8))
        }
}