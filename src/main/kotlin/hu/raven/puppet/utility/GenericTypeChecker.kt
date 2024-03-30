package hu.raven.puppet.utility

class GenericTypeChecker<T : Any>(val klass: Class<T>) {
    companion object {
        inline operator fun <reified T : Any> invoke() = GenericTypeChecker(T::class.java)
    }

    fun matches(t: Any): Boolean = when {
        klass.isAssignableFrom(t.javaClass) -> true
        else -> false
    }
}