package hu.raven.puppet.model.solution


import hu.akos.hollo.szabo.collections.swap

class PoolWithSmartActivation<T : HasId<Int>>(
    initialPool: Collection<T>
) {
    private val pool: MutableList<T> = initialPool.toMutableList()
    private val indexById = initialPool
        .withIndex()
        .sortedBy { it.value.id }
        .map { it.index }
        .toIntArray()
    var activeCount: Int = initialPool.size
        private set
    val poolSize: Int get() = pool.size

    fun activate(): Unit {
        if (activeCount == pool.size) return
        activeCount++
    }

    fun activate(id: Int): Unit {
        val index = indexById[id]
        if (index < activeCount) return
        val otherId = pool[activeCount].id
        indexById.swap(id, otherId)
        pool.swap(index, activeCount)
        activeCount++
    }

    fun deactivate(id: Int): Unit {
        val index = indexById[id]
        if (index >= activeCount) return
        activeCount--
        val otherId = pool[activeCount].id
        indexById.swap(id, otherId)
        pool.swap(index, activeCount)
    }

    fun activateAll(): Unit {
        activeCount = pool.size
    }

    fun deactivateAll(): Unit {
        activeCount = 0
    }

    fun isAllActive() = activeCount == pool.size

    fun activesAsSequence() =
        (0..<activeCount)
            .asSequence()
            .map { pool[it] }

    fun inactivesAsSequence() =
        (activeCount..<pool.size)
            .asSequence()
            .map { pool[it] }

    fun sortActiveBy(mapper: (T) -> Float): Unit {
        activesAsSequence()
            .sortedBy(mapper)
            .forEachIndexed { index, value ->
                pool[index] = value
            }
        updateIndex()
    }

    private fun updateIndex() {
        pool.forEachIndexed { index, value -> indexById[value.id] = index }
    }

    operator fun get(index: Int): T {
        if (index !in 0..<activeCount)
            throw IndexOutOfBoundsException("No active pool item on specified position!")
        return pool[index]
    }

    fun indexOf(item: T): Int {
        return indexById[item.id]
    }

    fun isActive(id: Int): Boolean {
        val index = indexById[id]
        return index < activeCount
    }


}