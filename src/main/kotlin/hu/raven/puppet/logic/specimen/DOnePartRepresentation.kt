package hu.raven.puppet.logic.specimen

import hu.raven.puppet.utility.extention.inverse
import hu.raven.puppet.utility.extention.isPermutation
import hu.raven.puppet.utility.extention.sequential
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

data class DOnePartRepresentation(
    override val id: Int,
    override val objectiveCount: Int,
    val permutation: IntArray,
    override var inUse: Boolean = true,
    override var costCalculated: Boolean = false,
    override var cost: Double = -1.0,
    override var iteration: Int = 0,
    override var orderInPopulation: Int = 0
) : ISpecimenRepresentation {

    private val lock = ReentrantReadWriteLock()

    constructor(id: Int, data: Array<IntArray>) : this(
        id,
        data.sumOf { it.size },
        data.let {
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
            permutation
        }
    )

    constructor(other: DOnePartRepresentation) : this(
        other.id,
        other.objectiveCount,
        other.permutation.clone(),
        other.inUse,
        other.costCalculated,
        other.cost,
        other.iteration
    )

    override val salesmanCount: Int = permutation.size - objectiveCount + 1

    override val permutationIndices: IntRange = permutation.indices
    override val permutationSize: Int = permutation.size

    override operator fun get(index: Int) = lock.read {
        permutation[index]
    }

    override operator fun set(index: Int, value: Int) = lock.write {
        permutation[index] = value
    }

    override fun indexOf(value: Int): Int = permutation.indexOf(value)

    override fun contains(value: Int): Boolean = permutation.contains(value)

    override fun <T> map(mapper: (Int) -> T): Sequence<T> = permutation.map(mapper).asSequence()

    override fun forEach(operation: (Int) -> Unit) =
        lock.read { permutation.forEach(operation) }

    override fun forEachIndexed(operation: (Int, Int) -> Unit) =
        lock.read { permutation.forEachIndexed(operation) }

    override fun setEach(operation: (Int, Int) -> Int) = lock.write {
        permutation.forEachIndexed { index: Int, value: Int -> permutation[index] = operation(index, value) }
    }

    override fun <T> mapSlice(mapper: (slice: IntArray) -> T): Collection<T> = lock.read {
        val result = mutableListOf<MutableList<Int>>(mutableListOf())
        permutation.forEach { value ->
            if (value < objectiveCount)
                result.last().add(value)
            else
                result.add(mutableListOf())
        }
        result.map { mapper(it.toIntArray()) }
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
        permutation.slice(indices)
    }

    override fun shuffle() = lock.write { permutation.shuffle() }
    override fun first(selector: (Int) -> Boolean): Int = lock.read { permutation.first(selector) }

    override fun setData(data: Collection<IntArray>) {
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
    }

    override fun getData(): Collection<IntArray> {
        lock.read {
            return mapSlice { list -> list }
        }
    }

    override fun checkFormat(): Boolean {
        val result = permutation.isPermutation()
        return if (permutation.filter { it >= objectiveCount }.size != salesmanCount - 1)
            false
        else result
    }

    override fun inverseOfPermutation() = permutation.inverse()

    override fun sequentialOfPermutation() = permutation.sequential()

    override fun copyOfPermutation() = permutation.copyOf()

    override fun <T : (Int, (Int) -> Int) -> Collection<Int>> copyOfPermutationBy(initializer: T) =
        initializer(permutation.size) { permutation[it] }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DOnePartRepresentation

        if (objectiveCount != other.objectiveCount) return false
        if (!permutation.contentEquals(other.permutation)) return false
        if (inUse != other.inUse) return false
        if (costCalculated != other.costCalculated) return false
        if (cost != other.cost) return false
        if (iteration != other.iteration) return false
        if (orderInPopulation != other.orderInPopulation) return false
        if (lock != other.lock) return false
        if (salesmanCount != other.salesmanCount) return false
        if (permutationIndices != other.permutationIndices) return false
        if (permutationSize != other.permutationSize) return false

        return true
    }

    override fun hashCode(): Int {
        var result = objectiveCount
        result = 31 * result + permutation.contentHashCode()
        result = 31 * result + inUse.hashCode()
        result = 31 * result + costCalculated.hashCode()
        result = 31 * result + cost.hashCode()
        result = 31 * result + iteration
        result = 31 * result + orderInPopulation
        result = 31 * result + lock.hashCode()
        result = 31 * result + salesmanCount
        result = 31 * result + permutationIndices.hashCode()
        result = 31 * result + permutationSize
        return result
    }
}
