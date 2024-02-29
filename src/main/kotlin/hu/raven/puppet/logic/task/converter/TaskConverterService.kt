package hu.raven.puppet.logic.task.converter


sealed class TaskConverterService<T, P> {
    abstract fun processRawTask(task: T): P
}