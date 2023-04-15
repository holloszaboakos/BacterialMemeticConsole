package hu.raven.puppet.model.solution

import hu.raven.puppet.utility.extention.swap

class PoolWithSmartActivation<T : HasId<Int>>(
    initialPool: Collection<T>
) {
    private val pool: MutableList<T> = initialPool.toMutableList()
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
        activeCount++
    }

    fun deactivate(index: Int) {
        if (index >= activeCount) return
        activeCount--
        pool.swap(index, activeCount)
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

    fun <C : Comparable<C>> sortActiveBy(mapper: (T) -> C) {
        activesAsSequence()
            .sortedBy(mapper)
            .forEachIndexed { index, value ->
                pool[index] = value
            }
    }

    operator fun get(index: Int): T {
        if (index !in 0 until activeCount)
            throw IndexOutOfBoundsException("No active pool item on specified position!")
        return pool[index]
    }


}