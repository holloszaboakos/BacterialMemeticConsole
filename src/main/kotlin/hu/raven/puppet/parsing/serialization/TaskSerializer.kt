package hu.raven.puppet.parsing.serialization

import hu.raven.puppet.parsing.model.TaskData
import java.nio.ByteBuffer

class TaskSerializer : ByteSerializer<TaskData> {
    override fun serialize(obj: TaskData): ByteArray {
        val buffer = ByteBuffer
            .allocate(obj.weightMatrix.size.let { it * it })

        buffer.putInt(obj.weightMatrix.size)
        for (column in obj.weightMatrix) {
            for (value in column) {
                buffer.putInt(value)
            }
        }

        return buffer.array()
    }

    override fun deserialize(bytes: ByteArray): TaskData {
        val buffer = ByteBuffer.wrap(bytes)

        val nodeCount = buffer.getInt()
        val weightMatrix = Array(nodeCount) {
            IntArray(nodeCount) {
                buffer.getInt()
            }
        }

        return TaskData(weightMatrix)
    }
}