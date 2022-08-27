package hu.raven.puppet.logic.specimen

import hu.raven.puppet.utility.extention.inverse
import hu.raven.puppet.utility.extention.isPermutation
import hu.raven.puppet.utility.extention.sequential

data class DTwoPartRepresentation(
    override val id:Int,
    val permutation: IntArray,
    val sliceLengths: IntArray,
    override var inUse: Boolean = true,
    override var costCalculated: Boolean = false,
    override var cost: Double = -1.0,
    override var iteration: Int = 0,
    override var orderInPopulation: Int = -1,
) : ISpecimenRepresentation {

    constructor(id:Int, data: Array<IntArray>) : this(
        id,
        data.let {
            val sum = mutableListOf<Int>()
            data.forEach { sum.addAll(it.toList()) }
            sum.toIntArray()
        },
        data.map { it.size }.toIntArray(),
    )

    constructor(other: DTwoPartRepresentation) : this(
        other.id,
        other.permutation.clone(),
        other.sliceLengths.clone(),
        other.inUse,
        other.costCalculated,
        other.cost,
        other.iteration
    )

    override val objectiveCount: Int = permutation.size
    override val salesmanCount: Int = sliceLengths.sum()
    override val permutationIndices: IntRange = permutation.indices
    override val permutationSize: Int = permutation.size

    override operator fun get(index: Int) = permutation[index]

    override operator fun set(index: Int, value: Int) {
        permutation[index] = value
    }

    override fun indexOf(value: Int): Int = permutation.indexOf(value)

    override fun contains(value: Int): Boolean = permutation.contains(value)

    override fun <T> map(mapper: (Int) -> T): Sequence<T> = permutation.map(mapper).asSequence()

    override fun forEach(operation: (Int) -> Unit) = permutation.forEach(operation)
    override fun forEachIndexed(operation: (Int, Int) -> Unit) = permutation.forEachIndexed(operation)

    override fun setEach(operation: (Int, Int) -> Int) {
        permutation.forEachIndexed { index: Int, value: Int -> permutation[index] = operation(index, value) }
    }

    override fun <T> mapSlice(mapper: (slice: IntArray) -> T): Collection<T> {
        var geneIndex = 0
        return sequence {
            for (sliceLength in sliceLengths) {
                val slice = permutation.slice(geneIndex until (geneIndex + sliceLength))
                geneIndex += sliceLength
                yield(mapper(slice.toIntArray()))
            }
        }.toList()
    }

    override fun forEachSlice(operation: (slice: IntArray) -> Unit) {
        var geneIndex = 0
        sliceLengths.forEach { sliceLength ->
            val slice = permutation.slice(geneIndex until (geneIndex + sliceLength))
            operation(slice.toIntArray())
            geneIndex += sliceLength
        }
    }

    override fun forEachSliceIndexed(operation: (index: Int, slice: IntArray) -> Unit) {
        var geneIndex = 0
        sliceLengths.forEachIndexed { index, sliceLength ->
            val slice = permutation.slice(geneIndex until (geneIndex + sliceLength))
            operation(index, slice.toIntArray())
            geneIndex += sliceLength
        }
    }

    override fun slice(indices: IntRange): Collection<Int> =
        permutation.slice(indices)


    override fun shuffle() {
        permutation.shuffle()
    }

    override fun first(selector: (Int) -> Boolean): Int = permutation.first(selector)

    override fun setData(data: Collection<IntArray>) {
        var shift = 0
        data.forEachIndexed { sliceIndex, slice ->
            sliceLengths[sliceIndex] = slice.toList().size
            slice.forEachIndexed { index, value -> permutation[shift + index] = value }
            shift += sliceLengths[sliceIndex]
        }
    }

    override fun getData(): Collection<IntArray> {
        return mapSlice { list -> list }
    }

    override fun checkFormat(): Boolean {
        val result = permutation.isPermutation()
        return if (sliceLengths.sum() != salesmanCount)
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

        other as DTwoPartRepresentation

        if (!permutation.contentEquals(other.permutation)) return false
        if (!sliceLengths.contentEquals(other.sliceLengths)) return false
        if (inUse != other.inUse) return false
        if (costCalculated != other.costCalculated) return false
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
        var result = permutation.contentHashCode()
        result = 31 * result + sliceLengths.contentHashCode()
        result = 31 * result + inUse.hashCode()
        result = 31 * result + costCalculated.hashCode()
        result = 31 * result + cost.hashCode()
        result = 31 * result + iteration
        result = 31 * result + orderInPopulation
        result = 31 * result + objectiveCount
        result = 31 * result + salesmanCount
        result = 31 * result + permutationIndices.hashCode()
        result = 31 * result + permutationSize
        return result
    }
}