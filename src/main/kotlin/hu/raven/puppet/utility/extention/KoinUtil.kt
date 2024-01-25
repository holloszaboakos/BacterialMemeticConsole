package hu.raven.puppet.utility.extention

import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

object KoinUtil {
    inline fun <reified T> get(): T {
        return KoinJavaComponent.get(T::class.java)
    }

    inline fun <reified T> get(name: String): T {
        return KoinJavaComponent.get(T::class.java, named(name))
    }

    inline fun <reified T, E : Enum<E>> get(name: E): T {
        return KoinJavaComponent.get(T::class.java, named(name))
    }
}
