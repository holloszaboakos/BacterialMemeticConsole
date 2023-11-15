package hu.raven.puppet.model.solution


import hu.raven.puppet.utility.extention.swap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class PoolWithSmartActivation<T : HasId<Int>>(
    initialPool: Collection<T>
) {
    private val lock = ReentrantReadWriteLock()
    private val pool: MutableList<T> = initialPool.toMutableList()
    private val indexById = initialPool
        .withIndex()
        .sortedBy { it.value.id }
        .map { it.index }
        .toIntArray()
    var activeCount: Int = lock.read { initialPool.size }
        private set
    val poolSize: Int get() = pool.size

    fun activate(): Unit = lock.write {
        if (activeCount == pool.size) return
        activeCount++
    }

    fun activate(id: Int): Unit = lock.write {
        val index = indexById[id]
        if (index < activeCount) return
        val otherId = pool[activeCount].id
        indexById.swap(id, otherId)
        pool.swap(index, activeCount)
        activeCount++
    }

    fun deactivate(id: Int): Unit = lock.write {
        val index = indexById[id]
        if (index >= activeCount) return
        activeCount--
        val otherId = pool[activeCount].id
        indexById.swap(id, otherId)
        pool.swap(index, activeCount)
    }

    fun activateAll(): Unit = lock.write {
        activeCount = pool.size
    }

    fun deactivateAll(): Unit = lock.write {
        activeCount = 0
    }

    fun isAllActive() = lock.write { activeCount == pool.size }

    fun activesAsSequence() = lock.read {
        (0 ..<activeCount)
            .asSequence()
            .map { pool[it] }
    }

    fun inactivesAsSequence() = lock.read {
        (activeCount ..<pool.size)
            .asSequence()
            .map { pool[it] }
    }

    fun sortActiveBy(mapper: (T) -> Float): Unit = lock.write {
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

    operator fun get(index: Int): T = lock.read {
        if (index !in 0 ..<activeCount)
            throw IndexOutOfBoundsException("No active pool item on specified position!")
        return pool[index]
    }

    fun indexOf(item: T): Int = lock.read {
        indexById[item.id]
    }

    fun isActive(id: Int): Boolean = lock.read {
        val index = indexById[id]
        return@read index < activeCount
    }


}