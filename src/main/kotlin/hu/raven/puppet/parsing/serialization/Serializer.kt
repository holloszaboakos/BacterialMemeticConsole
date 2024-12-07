package hu.raven.puppet.parsing.serialization

interface ByteSerializer<T> {
    fun serialize(obj: T): ByteArray
    fun deserialize(bytes: ByteArray): T
}