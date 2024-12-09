package hu.raven.puppet.parsing

import hu.raven.puppet.parsing.model.ConfigurationData
import hu.raven.puppet.parsing.model.StateData
import hu.raven.puppet.parsing.model.TaskData
import hu.raven.puppet.parsing.serialization.ConfigurationSerializer
import hu.raven.puppet.parsing.serialization.StateSerializer
import hu.raven.puppet.parsing.serialization.TaskSerializer
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

class ByteLogger(
    private var logsDirectory: Path
) {
    private fun createLogDirectory() {

        // Create the "logs" directory if it doesn't exist
        if (!java.nio.file.Files.exists(logsDirectory)) {
            java.nio.file.Files.createDirectory(logsDirectory)
        }

        // Generate timestamp for the subdirectory
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        val timestamp: String = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).format(formatter)

        // Create the timestamped subdirectory
        this.logsDirectory = logsDirectory.resolve(timestamp)
        if (!java.nio.file.Files.exists(this.logsDirectory)) {
            java.nio.file.Files.createDirectory(this.logsDirectory)
        }
    }

    fun load(logFilename: String): Triple<ConfigurationData, TaskData, StateData>? {
        println("Loading state from file: $logFilename")

        try {
            val metaDataFile = Paths.get(logsDirectory.toString(), "_metadata.log")
            val configurationSerializer = ConfigurationSerializer()
            val config = metaDataFile.toFile()
                .let(::FileInputStream)
                .let(::GZIPInputStream)
                .use { metaDataFileIn ->
                    configurationSerializer.deserialize(metaDataFileIn.readBytes())
                }

            val taskDataFile = Paths.get(logsDirectory.toString(), "_task.log")
            val taskSerializer = TaskSerializer()
            val task = taskDataFile.toFile()
                .let(::FileInputStream)
                .let(::GZIPInputStream)
                .use { taskDataFileIn ->
                    taskSerializer.deserialize(taskDataFileIn.readBytes())
                }

            val logFile = logsDirectory.resolve(logFilename)
            val stateSerializer = StateSerializer()
            val state = logFile.toFile()
                .let(::FileInputStream)
                .let(::GZIPInputStream)
                .use { stateDataFileIn ->
                    stateSerializer.deserialize(stateDataFileIn.readBytes())
                }
            return Triple(config, task, state)
        } catch (e: IOException) {
            System.err.println("Error loading state from file: " + e.message)
        } catch (e: ClassNotFoundException) {
            System.err.println("Error loading state from file: " + e.message)
        }

        return null
    }

    fun logConfiguration(taskData: TaskData) {
        val taskDataFile: Path = logsDirectory!!.resolve("_task.log.gz")
        val taskSerializer = TaskSerializer()

        taskDataFile.toFile()
            .let(::FileOutputStream)
            .let(::GZIPOutputStream)
            .use { taskDataFileOut ->
                taskDataFileOut.write(taskSerializer.serialize(taskData))
            }
    }

    fun logConfiguration(configurationData: ConfigurationData) {
        val metaDataFile: Path = logsDirectory!!.resolve("_metadata.log.gz")
        val configurationSerializer = ConfigurationSerializer()
        metaDataFile.toFile()
            .let(::FileOutputStream)
            .let(::GZIPOutputStream)
            .use { metaDataFileOut ->
                metaDataFileOut.write(configurationSerializer.serialize(configurationData))
            }
    }

    fun logState(stateData: StateData, configurationData: ConfigurationData) {
        val numberOfDigitsNeeded = configurationData.iterationLimit.minus(1).toString().length
        val fileName = String.format(
            "%0${numberOfDigitsNeeded}d-%s.log.gz",
            stateData.generationCount, stateData.phase
        )
        val logFile: Path = logsDirectory!!.resolve(fileName)

        val stateSerializer = StateSerializer()
        logFile.toFile()
            .let(::FileOutputStream)
            .let(::GZIPOutputStream)
            .use { logFileOut ->
                logFileOut.write(stateSerializer.serialize(stateData))
            }
    }
}
