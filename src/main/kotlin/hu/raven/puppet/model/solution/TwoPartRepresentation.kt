package hu.raven.puppet.model.solution

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.extention.asPermutation

data class TwoPartRepresentation<C : PhysicsUnit<C>>(
    override val id: Int,
    val permutation: Permutation,
    val sliceLengths: IntArray,
    override var inUse: Boolean = true,
    override var cost: C? = null,
    override var iteration: Int = 0,
    override var orderInPopulation: Int = -1,
) : SolutionRepresentation<C> {

    constructor(id: Int, data: Array<IntArray>) : this(
        id = id,
        permutation = data.let {
            val sum = mutableListOf<Int>()
            data.forEach { sum.addAll(it.toList()) }
            sum.toIntArray().asPermutation()
        },
        sliceLengths = data.map { it.size }.toIntArray()
    )

    constructor(other: TwoPartRepresentation<C>) : this(
        other.id,
        other.permutation.clone(),
        other.sliceLengths.clone(),
        other.inUse,
        other.cost,
        other.iteration
    )

    override val objectiveCount: Int = permutation.value.size
    override val salesmanCount: Int = sliceLengths.sum()
    override val permutationIndices: IntRange = permutation.value.indices
    override val permutationSize: Int = permutation.value.size

    override operator fun get(index: Int) = permutation.value[index]

    override operator fun set(index: Int, value: Int) {
        permutation.value[index] = value
    }

    override fun indexOf(value: Int): Int = permutation.value.indexOf(value)

    override fun contains(value: Int): Boolean = permutation.value.contains(value)

    override fun <T> map(mapper: (Int) -> T): Sequence<T> = permutation.value.map(mapper).asSequence()

    override fun forEach(operation: (Int) -> Unit) = permutation.value.forEach(operation)
    override fun forEachIndexed(operation: (Int, Int) -> Unit) = permutation.value.forEachIndexed(operation)

    override fun setEach(operation: (Int, Int) -> Int) {
        permutation.value.forEachIndexed { index: Int, value: Int ->
            permutation.value[index] = operation(index, value)
        }
    }

    override fun <T> mapSlice(mapper: (slice: IntArray) -> T): Collection<T> {
        var geneIndex = 0
        return sequence {
            for (sliceLength in sliceLengths) {
                val slice = permutation.value.slice(geneIndex until (geneIndex + sliceLength))
                geneIndex += sliceLength
                yield(mapper(slice.toIntArray()))
            }
        }.toList()
    }

    override fun <T> mapSliceIndexed(mapper: (index: Int, slice: IntArray) -> T): Collection<T> {
        var sliceIndex = 0
        var geneIndex = 0
        return sequence {
            for (sliceLength in sliceLengths) {
                val slice = permutation.value.slice(geneIndex until (geneIndex + sliceLength))
                geneIndex += sliceLength
                sliceIndex++
                yield(mapper(sliceIndex, slice.toIntArray()))
            }
        }.toList()
    }

    override fun forEachSlice(operation: (slice: IntArray) -> Unit) {
        var geneIndex = 0
        sliceLengths.forEach { sliceLength ->
            val slice = permutation.value.slice(geneIndex until (geneIndex + sliceLength))
            operation(slice.toIntArray())
            geneIndex += sliceLength
        }
    }

    override fun forEachSliceIndexed(operation: (index: Int, slice: IntArray) -> Unit) {
        var geneIndex = 0
        sliceLengths.forEachIndexed { index, sliceLength ->
            val slice = permutation.value.slice(geneIndex until (geneIndex + sliceLength))
            operation(index, slice.toIntArray())
            geneIndex += sliceLength
        }
    }

    override fun slice(indices: IntRange): Collection<Int> =
        permutation.value.slice(indices)


    override fun shuffle() {
        permutation.value.shuffle()
    }

    override fun first(selector: (Int) -> Boolean): Int = permutation.value.first(selector)

    override fun setData(data: Collection<IntArray>) {
        var shift = 0
        data.forEachIndexed { sliceIndex, slice ->
            sliceLengths[sliceIndex] = slice.toList().size
            slice.forEachIndexed { index, value -> permutation.value[shift + index] = value }
            shift += sliceLengths[sliceIndex]
        }
    }

    override fun getData(): Collection<IntArray> {
        return mapSlice { list -> list }
    }

    override fun checkFormat(): Boolean {
        val result = permutation.isNotMalformed()
        return if (sliceLengths.sum() != salesmanCount)
            false
        else result
    }

    override fun inverseOfPermutation() = permutation.inverse()

    override fun sequentialOfPermutation() = permutation.sequential()

    override fun copyOfPermutation() = permutation.value.copyOf()

    override fun <T : (Int, (Int) -> Int) -> Collection<Int>> copyOfPermutationBy(initializer: T) =
        initializer(permutation.value.size) { permutation.value[it] }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TwoPartRepresentation<*>

        if (id != other.id) return false
        if (permutation != other.permutation) return false
        if (!sliceLengths.contentEquals(other.sliceLengths)) return false
        if (inUse != other.inUse) return false
        if (cost != other.cost) return false
        if (iteration != other.iteration) return false
        if (orderInPopulation != other.orderInPopulation) return false
        if (objectiveCount != other.objectiveCount) return false
        if (salesmanCount != other.salesmanCount) return false
        if (permutationIndices != other.permutationIndices) return false
        if (permutationSize != other.permutationSize) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + permutation.hashCode()
        result = 31 * result + sliceLengths.contentHashCode()
        result = 31 * result + inUse.hashCode()
        result = 31 * result + (cost?.hashCode() ?: 0)
        result = 31 * result + iteration
        result = 31 * result + orderInPopulation
        result = 31 * result + objectiveCount
        result = 31 * result + salesmanCount
        result = 31 * result + permutationIndices.hashCode()
        result = 31 * result + permutationSize
        return result
    }
}