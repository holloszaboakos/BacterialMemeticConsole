package hu.raven.puppet.logic.step.common.logging

import kotlin.time.Duration

object CSVLineBuilderUtility {
    fun buildCsvLine(builder: StringBuilder.() -> Unit): String {
        val stringBuilder = StringBuilder()
        stringBuilder.builder()
        return stringBuilder.toString() + "\n"
    }

    fun <T> StringBuilder.appendField(value: T) {
        append("${value}\t")
    }

    fun StringBuilder.appendString(value: String) {
        append("\"${value}\"\t")
    }

    fun StringBuilder.appendDuration(value: Duration) {
        append("${value.inWholeMilliseconds}\t")
    }
}