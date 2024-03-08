package hu.raven.puppet.logic.logging

import hu.raven.puppet.model.logging.LogEvent

interface LoggingChannel<T> {
    fun initialize()
    fun toString(message: LogEvent<T>): String
    fun send(message: T)
}