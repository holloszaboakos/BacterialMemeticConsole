package hu.raven.puppet.model.solution

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.extention.asPermutation
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

data class OnePartRepresentation<C : PhysicsUnit<C>>(
    override val id: Int,
    override val objectiveCount: Int,
    val permutation: Permutation,
    override var inUse: Boolean = true,
    override var cost: C? = null,
    override var iteration: Int = 0,
    override var orderInPopulation: Int = 0
) : SolutionRepresentation<C> {

    private val lock = ReentrantReadWriteLock()

    constructor(id: Int, data: Array<IntArray>) : this(
        id = id,
        objectiveCount = data.sumOf { it.size },
        permutation = data.let {
            val objectiveCount = data.sumOf { it.size }
            val salesmanCount = data.size
            val permutation = IntArray(objectiveCount + salesmanCount - 1) { -1 }
            var counter = 0
            data.forEachIndexed { index, array ->
                array.forEach {
                    permutation[counter] = it
                    counter++
                }
                if (counter < permutation.size) {
                    permutation[counter] = objectiveCount + index
                    counter++
                }
            }
            permutation.asPermutation()
        }
    )

    constructor(other: OnePartRepresentation<C>) : this(
        other.id,
        other.objectiveCount,
        other.permutation.clone(),
        other.inUse,
        other.cost,
        other.iteration
    )

    override val salesmanCount: Int = permutation.value.size - objectiveCount + 1

    override val permutationIndices: IntRange = permutation.value.indices
    override val permutationSize: Int = permutation.value.size

    override operator fun get(index: Int) = lock.read {
        permutation.value[index]
    }

    override operator fun set(index: Int, value: Int) = lock.write {
        permutation.value[index] = value
    }

    override fun indexOf(value: Int): Int = permutation.value.indexOf(value)

    override fun contains(value: Int): Boolean = permutation.value.contains(value)

    override fun <T> map(mapper: (Int) -> T): Sequence<T> = permutation.value.map(mapper).asSequence()

    override fun forEach(operation: (Int) -> Unit) =
        lock.read { permutation.value.forEach(operation) }

    override fun forEachIndexed(operation: (Int, Int) -> Unit) =
        lock.read { permutation.value.forEachIndexed(operation) }

    override fun setEach(operation: (Int, Int) -> Int) = lock.write {
        permutation.value.forEachIndexed { index: Int, value: Int ->
            permutation.value[index] = operation(index, value)
        }
    }

    override fun <T> mapSlice(mapper: (slice: IntArray) -> T): Collection<T> = lock.read {
        val result = mutableListOf<MutableList<Int>>(mutableListOf())
        permutation.value.forEach { value ->
            if (value < objectiveCount)
                result.last().add(value)
            else
                result.add(mutableListOf())
        }
        result.map { mapper(it.toIntArray()) }
    }

    override fun <T> mapSliceIndexed(mapper: (index: Int, slice: IntArray) -> T): Collection<T> = lock.read {
        val result = mutableListOf<MutableList<Int>>(mutableListOf())
        permutation.value.forEach { value ->
            if (value < objectiveCount)
                result.last().add(value)
            else
                result.add(mutableListOf())
        }
        result.mapIndexed { index, it -> mapper(index, it.toIntArray()) }
    }

    override fun forEachSlice(operation: (slice: IntArray) -> Unit) {
        mapSlice { it }
            .toList()
            .forEach { slice -> operation(slice) }

    }

    override fun forEachSliceIndexed(operation: (index: Int, slice: IntArray) -> Unit) {
        mapSlice { it }
            .toList()
            .forEachIndexed { index, flow ->
                operation(index, flow)
            }

    }

    override fun slice(indices: IntRange): Collection<Int> = lock.read {
        permutation.value.slice(indices)
    }

    override fun shuffle() = lock.write { permutation.value.shuffle() }
    override fun first(selector: (Int) -> Boolean): Int = lock.read { permutation.value.first(selector) }

    override fun setData(data: Collection<IntArray>) {
        var counter = 0
        data.forEachIndexed { index, array ->
            array.forEach {
                permutation.value[counter] = it
                counter++
            }
            if (counter < permutation.value.size) {
                permutation.value[counter] = objectiveCount + index
                counter++
            }
        }
    }

    override fun getData(): Collection<IntArray> {
        lock.read {
            return mapSlice { list -> list }
        }
    }

    override fun checkFormat(): Boolean {
        val result = permutation.isPermutation()
        return if (permutation.value.filter { it >= objectiveCount }.size != salesmanCount - 1)
            false
        else result
    }

    override fun inverseOfPermutation() = permutation.inverse()

    override fun sequentialOfPermutation() = permutation.sequential()

    override fun copyOfPermutation() = permutation.value.copyOf()

    override fun <T : (Int, (Int) -> Int) -> Collection<Int>> copyOfPermutationBy(initializer: T) =
        initializer(permutation.value.size) { permutation.value[it] }

}
