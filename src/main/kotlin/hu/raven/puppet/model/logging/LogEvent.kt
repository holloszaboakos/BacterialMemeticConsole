package hu.raven.puppet.model.logging

import java.time.LocalDateTime

public data class LogEvent<T>(
    var time: LocalDateTime,
    var type: LogType,
    var source: String,
    var version: Int,
    var message: T
)
