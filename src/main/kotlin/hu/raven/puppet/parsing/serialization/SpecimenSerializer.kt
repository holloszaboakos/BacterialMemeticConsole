package hu.raven.puppet.parsing.serialization

import hu.raven.puppet.parsing.model.SpecimenData
import java.nio.ByteBuffer

class SpecimenSerializer : ByteSerializer<SpecimenData> {

    override fun serialize(obj: SpecimenData): ByteArray {
        val buffer = ByteBuffer
            .allocate(Int.SIZE_BYTES * (2 + obj.permutation.size))

        buffer.putInt(obj.id)
        buffer.putInt(obj.permutation.size)
        for (value in obj.permutation) {
            buffer.putInt(value)
        }

        return buffer.array()
    }

    override fun deserialize(bytes: ByteArray): SpecimenData {
        val buffer = ByteBuffer.wrap(bytes)

        val id = buffer.getInt()
        val permutationSize = buffer.getInt()
        val permutation = IntArray(permutationSize) {
            buffer.getInt()
        }

        return SpecimenData(id, permutation)
    }
}