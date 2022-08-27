package hu.raven.puppet.utility.extention

import kotlinx.coroutines.flow.*

fun <T> Flow<T>.slice(range: IntRange): Flow<T> = flow {
    collectIndexed { index, item ->
        if (index in range) {
            emit(item)
            return@collectIndexed
        }
    }
}


suspend fun <T> Flow<T>.shuffled(): Flow<T> {
    return toList()
        .asSequence()
        .shuffled()
        .asFlow()
}