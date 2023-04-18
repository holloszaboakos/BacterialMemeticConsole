package hu.raven.puppet.logic.logging

data class CsvHeader<T : Any>(
    val displayText: String,
    val fieldExtractor: (T) -> String
)