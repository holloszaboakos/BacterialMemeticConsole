package hu.raven.puppet.parsing.serialization

import hu.raven.puppet.parsing.model.AlgorithmPhase
import hu.raven.puppet.parsing.model.StateData
import java.nio.ByteBuffer

class StateSerializer : ByteSerializer<StateData> {
    val specimenSerializer = SpecimenSerializer()
    override fun serialize(obj: StateData): ByteArray {
        val specimenSerialized = obj.specimens
            .map { specimenSerializer.serialize(it) }

        val buffer = ByteBuffer
            .allocate(
                4 * Int.SIZE_BYTES + specimenSerialized.size * specimenSerialized[0].size
            )

        buffer.putInt(obj.phase.ordinal)
        buffer.putInt(obj.generationCount)
        buffer.putInt(obj.specimens.size)
        buffer.putInt(specimenSerialized[0].size)
        for (specimen in specimenSerialized) {
            buffer.put(specimen)
        }

        return buffer.array()
    }

    override fun deserialize(bytes: ByteArray): StateData {
        val buffer = ByteBuffer.wrap(bytes)

        val phase = AlgorithmPhase.entries[buffer.getInt()]
        //GenerationNumber
        val generationNum = buffer.getInt()
        //Specimens
        val specimenCount = buffer.getInt()
        val specimenSize = buffer.getInt()
        val specimens = Array(specimenCount) {
            ByteArray(specimenSize) {
                buffer.get()
            }.let {
                specimenSerializer.deserialize(it)
            }

        }

        return StateData(phase, generationNum, specimens)
    }
}