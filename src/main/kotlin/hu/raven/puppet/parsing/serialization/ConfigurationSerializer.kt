package hu.raven.puppet.parsing.serialization

import hu.raven.puppet.parsing.model.ConfigurationData
import java.nio.ByteBuffer

class ConfigurationSerializer : ByteSerializer<ConfigurationData> {
    override fun serialize(obj: ConfigurationData): ByteArray {
        val buffer = ByteBuffer
            .allocate(Int.SIZE_BYTES + Float.SIZE_BYTES)

        buffer.putInt(obj.specimenCount)
        buffer.putInt(obj.iterationLimit)
        buffer.putFloat(obj.selectionPercentage)

        return buffer.array()
    }

    override fun deserialize(bytes: ByteArray): ConfigurationData {
        val buffer = ByteBuffer.wrap(bytes)

        val specimenCount = buffer.getInt()
        val iterationLimit = buffer.getInt()
        val selectionPercentage = buffer.getFloat()

        return ConfigurationData(specimenCount, iterationLimit, selectionPercentage)
    }
}