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

    fun activesAsSequence() =
        (0 until activeCount)
            .asSequence()
            .map { pool[it] }

    fun inactivesAsSequence() =
        (activeCount until pool.size)
            .asSequence()
            .map { pool[it] }

    fun <C : Comparable<C>> sortActiveBy(mapper: (PoolItem<T>) -> C) {
        activesAsSequence()
            .sortedBy(mapper)
            .forEachIndexed { index, value ->
                value.index = index
                pool[index] = value
            }
    }

    operator fun get(index: Int): PoolItem<T> {
        if (index !in 0 until activeCount)
            throw IndexOutOfBoundsException("No active pool item on specified position!")
        return pool[index]
    }


}