package hu.raven.puppet.utility.extention

inline fun <reified T : Any> Any.tryCast(): T? {
    if (this !is T)
        return null

    return this
}

inline fun <reified T : Any> Any.runIfInstanceOf(processing: T.() -> Unit) {
    if (this is T)
        processing()
}

inline fun <reified T : Any> Any.runIfInstanceOfOrException(processing: (T) -> Unit) {
    if (this !is T)
        throw Exception("Subject is not of expected type")

    processing(this)
}