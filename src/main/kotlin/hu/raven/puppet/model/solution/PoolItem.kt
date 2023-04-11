package hu.raven.puppet.model.solution

data class PoolItem<T>(
    val id: Int,
    var index: Int,
    var content: T,
)