package hu.raven.puppet.job

import hu.akos.hollo.szabo.collections.asImmutable
import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.asDoubleVector
import hu.akos.hollo.szabo.math.asIntVector
import hu.akos.hollo.szabo.math.matrix.DoubleMatrix
import hu.akos.hollo.szabo.math.matrix.IntMatrix
import hu.akos.hollo.szabo.math.toDoubleVector
import hu.raven.puppet.model.utility.math.GraphEdge
import java.io.File

fun main() {
    val regretRecords = loadRegrets()
    regretRecords.asSequence()
        .map { record ->
            val optimalEdges = record.expectedRegretMatrix
                .mapEachEntryIndexed { columnIndex, rowIndex, value ->
                    GraphEdge(columnIndex, rowIndex, value)
                }
                .flatten()
                .filter { it.sourceNodeIndex != it.targetNodeIndex }
                .filter { it.value == 0.0 }
                .toList()
                .groupBy { it.sourceNodeIndex }
                .mapValues { (_, value) -> value.map { it.targetNodeIndex } }
                .entries
                .sortedBy { it.key }
                .map { it.value }
                .toTypedArray()

            var permutations = mutableListOf<Permutation>()

            optimalEdges.last().forEach { targetNode ->
                permutations.add(
                    Permutation(size = 127).apply { set(0, targetNode) }
                )
            }

            for (nextIndex in 1 until 127) {
                val newPermutations = mutableListOf<Permutation>()
                permutations.forEach { permutation ->
                    val lastValue = permutation[nextIndex - 1]
                    val nextValueOptions = optimalEdges[lastValue]
                        .filter { it != 127 }
                        .filter { !permutation.contains(it) }

                    nextValueOptions.forEach { nextValue ->
                        newPermutations.add(
                            permutation.clone().apply { set(nextIndex, nextValue) }
                        )
                    }
                }
                permutations = newPermutations
            }

            println(permutations)
        }
        .toList()
}

data class RegretData(
    val distanceMatrix: IntMatrix,
    val expectedRegretMatrix: DoubleMatrix,
    val predictedRegretMatrix: DoubleMatrix,
    val optCost: Long
)

fun loadRegrets(): List<RegretData> {
    val sourceFolder = File("D:\\Research\\Datasets\\tsp64x10_000-regert-2024-04-13")
    return sourceFolder.listFiles().asSequence()
        .map { file ->
            val distanceMatrix = file.useLines { lines ->
                lines
                    .drop(1)
                    .takeWhile { it.isNotBlank() }
                    .map { line ->
                        line
                            .split(" ")
                            .map { it.toDouble().toInt() }
                            .toIntArray()
                            .asIntVector()
                    }
                    .toList()
                    .toTypedArray()
                    .asImmutable()
                    .let { rows -> IntMatrix(rows) }
            }

            val expectedRegretMatrix = file.useLines { lines ->
                var wasStartLabel = false
                lines
                    .dropWhile {
                        wasStartLabel = wasStartLabel || it == "regret:"
                        it == "regret:" || !wasStartLabel
                    }
                    .takeWhile { it.isNotBlank() }
                    .map { line ->
                        line
                            .split(" ")
                            .map { it.toDouble() }
                            .toDoubleArray()
                            .asDoubleVector()
                    }
                    .toList()
                    .toTypedArray()
                    .asImmutable()
                    .let { rows -> DoubleMatrix(rows) }
            }

            val predictedRegretMatrix = file.useLines { lines ->
                var wasStartLabel = false
                lines
                    .dropWhile {
                        wasStartLabel = wasStartLabel || it == "regret_pred:"
                        it == "regret_pred:" || !wasStartLabel
                    }
                    .takeWhile { it.isNotBlank() }
                    .map { line ->
                        line
                            .split(" ")
                            .map { it.toDouble() }
                            .toDoubleArray()
                            .toDoubleVector()
                    }
                    .toList()
                    .toTypedArray()
                    .asImmutable()
                    .let { rows -> DoubleMatrix(rows) }
            }

            val optCost = file.useLines { lines ->
                lines.first { it.startsWith("opt_cost") }.split(" ")[1].toDouble().toLong()
            }

            RegretData(
                distanceMatrix = distanceMatrix,
                expectedRegretMatrix = expectedRegretMatrix,
                predictedRegretMatrix = predictedRegretMatrix,
                optCost = optCost
            )
        }
        .toList()
}