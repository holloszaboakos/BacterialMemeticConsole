package hu.raven.puppet.utility

import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent


inline fun <reified T> inject(): Lazy<T> {
    return KoinJavaComponent.inject(T::class.java)
}

inline fun <reified T> inject(name: String): Lazy<T> {
    return KoinJavaComponent.inject(T::class.java, named(name))
}

inline fun <reified T, E : Enum<E>> inject(name: E): Lazy<T> {
    return KoinJavaComponent.inject(T::class.java, named(name))
}

inline fun <reified T> get(): T {
    return KoinJavaComponent.get(T::class.java)
}

inline fun <reified T> get(name: String): T {
    return KoinJavaComponent.get(T::class.java, named(name))
}

inline fun <reified T, E : Enum<E>> get(name: E): T {
    return KoinJavaComponent.get(T::class.java, named(name))
}