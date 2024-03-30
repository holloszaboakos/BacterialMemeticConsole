package hu.raven.puppet.utility

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object LocalDateTypeAdapter : TypeAdapter<LocalDate>() {
    override fun write(output: JsonWriter, value: LocalDate) {
        output.value(value.format(DateTimeFormatter.ISO_DATE))
    }

    override fun read(input: JsonReader): LocalDate {
        return LocalDate.parse(
            input.nextString(),
            DateTimeFormatter.ISO_DATE
        )
    }
}