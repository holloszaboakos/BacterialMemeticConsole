package hu.raven.puppet.logic.task.converter

import hu.raven.puppet.model.task.Task

sealed class TaskConverterService<T> {
    protected abstract val vehicleCount: Int
    abstract fun toStandardTask(task: T): Task
}