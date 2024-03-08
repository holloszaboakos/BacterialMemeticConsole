package hu.raven.puppet.utility

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LocalDateTimTypeAdapter : TypeAdapter<LocalDateTime>() {
    override fun write(output: JsonWriter, value: LocalDateTime) {
        output.value(value.format(DateTimeFormatter.ISO_DATE_TIME))
    }

    override fun read(input: JsonReader): LocalDateTime {
        return LocalDateTime.parse(
            input.nextString(),
            DateTimeFormatter.ISO_DATE_TIME
        )
    }
}