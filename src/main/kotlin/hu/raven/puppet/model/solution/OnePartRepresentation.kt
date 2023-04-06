package hu.raven.puppet.model.solution

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.extention.asPermutation
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

data class OnePartRepresentation<C : PhysicsUnit<C>>(
    val id: Int,
    val objectiveCount: Int,
    val permutation: Permutation,
    var inUse: Boolean = true,
    var cost: C? = null,
    var iteration: Int = 0,
    var orderInPopulation: Int = 0
) {

    fun costOrException() = cost ?: throw Exception("Cost of specimen should be already set!")
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

    val salesmanCount: Int = permutation.value.size - objectiveCount + 1

    val permutationIndices: IntRange = permutation.value.indices
    val permutationSize: Int = permutation.value.size

    operator fun get(index: Int) = lock.read {
        permutation.value[index]
    }

    operator fun set(index: Int, value: Int) = lock.write {
        permutation.value[index] = value
    }

    fun indexOf(value: Int): Int = permutation.value.indexOf(value)

    fun contains(value: Int): Boolean = permutation.value.contains(value)

    fun <T> map(mapper: (Int) -> T): Sequence<T> = permutation.value.map(mapper).asSequence()

    fun forEach(operation: (Int) -> Unit) =
        lock.read { permutation.value.forEach(operation) }

    fun forEachIndexed(operation: (Int, Int) -> Unit) =
        lock.read { permutation.value.forEachIndexed(operation) }

    fun setEach(operation: (Int, Int) -> Int) = lock.write {
        permutation.value.forEachIndexed { index: Int, value: Int ->
            permutation.value[index] = operation(index, value)
        }
    }

    fun <T> mapSlice(mapper: (slice: IntArray) -> T): Collection<T> = lock.read {
        val result = mutableListOf<MutableList<Int>>(mutableListOf())
        permutation.value.forEach { value ->
            if (value < objectiveCount)
                result.last().add(value)
            else
                result.add(mutableListOf())
        }
        result.map { mapper(it.toIntArray()) }
    }

    fun <T> mapSliceIndexed(mapper: (index: Int, slice: IntArray) -> T): Collection<T> = lock.read {
        val result = mutableListOf<MutableList<Int>>(mutableListOf())
        permutation.value.forEach { value ->
            if (value < objectiveCount)
                result.last().add(value)
            else
                result.add(mutableListOf())
        }
        result.mapIndexed { index, it -> mapper(index, it.toIntArray()) }
    }

    fun forEachSlice(operation: (slice: IntArray) -> Unit) {
        mapSlice { it }
            .toList()
            .forEach { slice -> operation(slice) }

    }

    fun forEachSliceIndexed(operation: (index: Int, slice: IntArray) -> Unit) {
        mapSlice { it }
            .toList()
            .forEachIndexed { index, flow ->
                operation(index, flow)
            }

    }

    fun slice(indices: IntRange): Collection<Int> = lock.read {
        permutation.value.slice(indices)
    }

    fun shuffle() = lock.write { permutation.value.shuffle() }
    fun first(selector: (Int) -> Boolean): Int = lock.read { permutation.value.first(selector) }

    fun setData(data: Collection<IntArray>) {
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

    fun getData(): Collection<IntArray> {
        lock.read {
            return mapSlice { list -> list }
        }
    }

    fun checkFormat(): Boolean {
        val result = permutation.isNotMalformed()
        return if (permutation.value.filter { it >= objectiveCount }.size != salesmanCount - 1)
            false
        else result
    }

    fun inverseOfPermutation() = permutation.inverse()

    fun sequentialOfPermutation() = permutation.sequential()

    fun copyOfPermutation() = permutation.value.copyOf()

    fun <T : (Int, (Int) -> Int) -> Collection<Int>> copyOfPermutationBy(initializer: T) =
        initializer(permutation.value.size) { permutation.value[it] }

    fun swapGenes(firstIndex: Int, secondIndex: Int) {
        val tempGene = this[firstIndex]
        this[firstIndex] = this[secondIndex]
        this[secondIndex] = tempGene
    }
}
