package hu.raven.puppet.model.solution

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit

sealed interface SolutionRepresentation<C : PhysicsUnit<C>> {
    val id: Int
    var inUse: Boolean
    var cost: C?
    var iteration: Int
    var orderInPopulation: Int
    val objectiveCount: Int
    val salesmanCount: Int
    val permutationIndices: IntRange
    val permutationSize: Int

    fun costOrException() = cost ?: throw Exception("Cost of specimen should be already set!")

    operator fun get(index: Int): Int
    operator fun set(index: Int, value: Int)

    fun indexOf(value: Int): Int
    fun contains(value: Int): Boolean

    fun <T> map(mapper: (value: Int) -> T): Sequence<T>
    fun forEach(operation: (value: Int) -> Unit)
    fun forEachIndexed(operation: (index: Int, value: Int) -> Unit)
    fun setEach(operation: (index: Int, value: Int) -> Int)

    fun <T> mapSlice(mapper: (slice: IntArray) -> T): Collection<T>
    fun <T> mapSliceIndexed(mapper: (index: Int, slice: IntArray) -> T): Collection<T>
    fun forEachSlice(operation: (slice: IntArray) -> Unit)
    fun forEachSliceIndexed(operation: (index: Int, slice: IntArray) -> Unit)
    fun slice(indices: IntRange): Collection<Int>

    fun shuffle()
    fun first(selector: (value: Int) -> Boolean): Int

    fun setData(data: Collection<IntArray>)
    fun getData(): Collection<IntArray>

    fun checkFormat(): Boolean

    fun inverseOfPermutation(): Permutation
    fun sequentialOfPermutation(): IntArray
    fun copyOfPermutation(): IntArray
    fun <T : (Int, (Int) -> Int) -> Collection<Int>> copyOfPermutationBy(initializer: T): Collection<Int>

    fun swapGenes(firstIndex: Int, secondIndex: Int) {
        val tempGene = this[firstIndex]
        this[firstIndex] = this[secondIndex]
        this[secondIndex] = tempGene
    }
}
