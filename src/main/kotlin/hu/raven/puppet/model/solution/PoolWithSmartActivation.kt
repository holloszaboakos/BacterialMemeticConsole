package hu.raven.puppet.model.solution

import hu.raven.puppet.utility.extention.swap

class PoolWithSmartActivation<T>(
    initialPool: Collection<T>
) {
    private val pool: MutableList<PoolItem<T>> = initialPool
        .mapIndexed { index, value ->
            PoolItem(index, index, value)
        }
        .toMutableList()
    var activeCount: Int = initialPool.size
        private set
    val maxSize: Int get() = pool.size

    fun activate() {
        if (activeCount == pool.size) return
        activeCount++
    }

    fun activate(index: Int) {
        if (index < activeCount) return
        pool.swap(index, activeCount)
        pool[activeCount].index = activeCount
        pool[index].index = index
        activeCount++
    }

    fun deactivate(index: Int) {
        if (index >= activeCount) return
        activeCount--
        pool.swap(index, activeCount)
        pool[activeCount].index = activeCount
        pool[index].index = index
    }

    fun activateAll() {
        activeCount = pool.size
    }

    fun deactivateAll() {
        activeCount = 0
    }

    fun isAllActive() = activeCount == pool.size

    fun foreachActive(action: (PoolItem<T>) -> Unit) =
        (0 until activeCount)
            .map { pool[it] }
            .forEach(action)

    fun foreachInactive(action: (PoolItem<T>) -> Unit) =
        (activeCount until pool.size)
            .map { pool[it] }
            .forEach(action)

    fun <R> mapActives(mapper: (PoolItem<T>) -> R) =
        (0 until activeCount)
            .map { pool[it] }
            .map(mapper)

    fun <R> mapInactive(mapper: (PoolItem<T>) -> R) =
        (activeCount until pool.size)
            .map { pool[it] }
            .map(mapper)

    fun <C : Comparable<C>> sortActiveBy(mapper: (PoolItem<T>) -> C) {
        mapActives { it }
            .sortedBy(mapper)
            .forEachIndexed { index, value ->
                value.index = index
                pool[index] = value
            }
    }


}