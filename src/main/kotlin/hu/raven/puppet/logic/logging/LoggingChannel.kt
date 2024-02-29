package hu.raven.puppet.logic.logging

interface LoggingChannel<in T> {
    fun initialize()
    fun toString(message: T): String
    fun send(message: T)
}