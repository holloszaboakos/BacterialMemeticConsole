package hu.raven.puppet.job

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.logic.statistics.edgeHistogramMatrix
import hu.raven.puppet.model.state.BacteriophageAlgorithmState
import java.io.File

fun main() {
    val file = File("output/2024-02-29/2024-02-29T21_01_58_403359600/algorithmState.json")
    val gson = GsonBuilder()
        .registerTypeAdapter(Permutation::class.java, object : TypeAdapter<Permutation>() {
            override fun write(out: JsonWriter, value: Permutation) {
                out.beginArray()
                value.forEach { out.value(it) }
                out.endArray()
            }

            override fun read(input: JsonReader): Permutation {
                input.beginArray()
                val values = buildList {
                    while (input.hasNext()) {
                        add(input.nextInt())
                    }
                }
                input.endArray()
                return Permutation(values)
            }
        })
        .create()
    val states = file.useLines { lines ->
        lines
            .mapIndexed { index, line ->
                println(index)
                gson.fromJson(line, BacteriophageAlgorithmState::class.java)
            }
            .map { edgeHistogramMatrix(it) }
            .toList()
    }
    println(states)
}
