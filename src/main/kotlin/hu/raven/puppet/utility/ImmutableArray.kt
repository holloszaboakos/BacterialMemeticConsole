package hu.raven.puppet.utility

data class ImmutableArray<T>(private val array: Array<T>) {
    val size: Int get() = array.size

    companion object {
        inline fun <reified T> immutableArrayOf(vararg values: T) = arrayOf(*values).asImmutable()
        inline fun <reified T> Array<T>.asImmutable() = ImmutableArray(this)
    }

    operator fun get(index: Int) = array[index]
    fun <R> contentEquals(other: ImmutableArray<R>) = array.contentEquals(other.array)
    fun contentHashCode() = array.contentHashCode()
    fun <R> map(mapper: (T) -> R) = array.map(mapper)
    fun <R> flatMap(mapper: (T) -> Sequence<R>) = array.flatMap(mapper)
    fun <R> mapIndexed(mapper: (Int, T) -> R) = array.mapIndexed(mapper)
    fun <R : Comparable<R>> minOfOrNull(mapper: (T) -> R) = array.minOfOrNull(mapper)
    fun first() = array.first()
    fun first(predicate: (T) -> Boolean) = array.first(predicate)
    fun firstOrNull(predicate: (T) -> Boolean) = array.firstOrNull(predicate)
    fun all(predicate: (T) -> Boolean) = array.all(predicate)
    fun filter(predicate: (T) -> Boolean) = array.filter(predicate)
    fun indexOfFirst(predicate: (T) -> Boolean) = array.indexOfFirst(predicate)
    fun withIndex() = array.withIndex()
    fun asSequence() = array.asSequence()
    fun forEach(function: (T) -> Unit) = array.forEach(function)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImmutableArray<*>

        if (!array.contentEquals(other.array)) return false

        return true
    }

    override fun hashCode(): Int {
        return array.contentHashCode()
    }

    override fun toString(): String = array.contentDeepToString()
}

fun <R> ImmutableArray<ImmutableArray<R>>.flatten() = flatMap { row -> row.asSequence() }