package hu.raven.puppet.utility

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import hu.akos.hollo.szabo.math.Permutation

object PermutationTypeAdapter : TypeAdapter<Permutation>() {
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
}

