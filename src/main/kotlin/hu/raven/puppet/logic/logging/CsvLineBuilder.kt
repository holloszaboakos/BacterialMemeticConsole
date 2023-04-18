package hu.raven.puppet.logic.logging

import kotlin.time.Duration

@JvmInline
value class CsvLineBuilder private constructor(private val builder: StringBuilder) {
    companion object {
        fun buildCsvLine(buildCsvLine: CsvLineBuilder.() -> Unit): String {
            val csvBuilder = CsvLineBuilder(StringBuilder())
            csvBuilder.buildCsvLine()
            return csvBuilder.builder.toString() + "\n"
        }
    }

    fun <T> appendField(value: T) {
        builder.append("${value}\t")
    }

    fun appendString(value: String) {
        builder.append("\"${value}\"\t")
    }

    fun appendDuration(value: Duration) {
        builder.append("${value.inWholeMilliseconds}\t")
    }
}