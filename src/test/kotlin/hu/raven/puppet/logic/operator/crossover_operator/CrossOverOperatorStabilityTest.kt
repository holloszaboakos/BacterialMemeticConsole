package hu.raven.puppet.logic.operator.crossover_operator

import hu.akos.hollo.szabo.collections.asImmutable
import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.matrix.FloatMatrix
import hu.akos.hollo.szabo.math.vector.IntVector
import hu.akos.hollo.szabo.math.vector.IntVector2D
import hu.akos.hollo.szabo.physics.CubicMeter
import hu.akos.hollo.szabo.physics.Gram
import hu.akos.hollo.szabo.physics.Meter
import hu.akos.hollo.szabo.physics.Second
import hu.raven.puppet.model.utility.Gps
import hu.raven.puppet.model.utility.math.CompleteGraphWithCenterVertex
import hu.raven.puppet.model.utility.math.GraphEdge
import hu.raven.puppet.model.utility.math.GraphVertex
import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertTrue

class CrossOverOperatorStabilityTest {
    companion object {
        const val REPEAT_TIME = 10_000_000
        const val PROBLEM_SIZE = 10
    }

    @Test
    fun alternatingEdgeCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            AlternatingEdgeCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.isFormatCorrect())
        }
    }

    @Test
    fun alternatingPositionCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            AlternatingPositionCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.isFormatCorrect())
        }
    }

    @Test
    fun cycleCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            CycleCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.isFormatCorrect())
        }
    }

    @Test
    fun distancePreservingCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            DistancePreservingCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.isFormatCorrect())
        }
    }

    @Test
    fun geneticEdgeRecombinationCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            GeneticEdgeRecombinationCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.isFormatCorrect())
        }
    }

    @Test
    fun heuristicCrossOver() {
        val costGraph = FloatMatrix(IntVector2D(PROBLEM_SIZE + 1, PROBLEM_SIZE + 1)) { Random.nextFloat() }
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            HeuristicCrossOver(costGraph)(Pair(firstParent, secondParent), child)
            assertTrue(child.isFormatCorrect())
        }
    }

    @Test
    fun maximalPreservationCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            MaximalPreservationCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.isFormatCorrect())
        }
    }

    @Test
    fun orderBasedCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            OrderBasedCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.isFormatCorrect())
        }
    }

    @Test
    fun orderCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            OrderCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.isFormatCorrect())
        }
    }

    @Test
    fun partiallyMatchedCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            PartiallyMatchedCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.isFormatCorrect())
        }
    }

    @Test
    fun positionBasedCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            PositionBasedCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.isFormatCorrect())
        }
    }

    @Test
    fun sortedMatchCrossOver() {
        val costGraph = CompleteGraphWithCenterVertex(
            centerVertex = Unit,
            vertices = Array(PROBLEM_SIZE) {
                GraphVertex(
                    index = it,
                    value = Unit
                )
            }.asImmutable(),
            edgesBetween = Array(PROBLEM_SIZE) {sourceNodeIndex->
                Array(PROBLEM_SIZE) {targetNodeIndex->
                    GraphEdge(
                        sourceNodeIndex = sourceNodeIndex,
                        targetNodeIndex = targetNodeIndex,
                        value = (Random.nextInt())
                    )
                }.asImmutable()
            }.asImmutable(),
            edgesFromCenter = Array(PROBLEM_SIZE) {
                GraphEdge(
                    sourceNodeIndex = PROBLEM_SIZE,
                    targetNodeIndex = it,
                    value = (Random.nextInt())
                )
            }.asImmutable(),
            edgesToCenter = Array(PROBLEM_SIZE) {
                GraphEdge(
                    sourceNodeIndex = it,
                    targetNodeIndex = PROBLEM_SIZE,
                    value = (Random.nextInt())
                )
            }.asImmutable(),
        )

        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            SortedMatchCrossOver(costGraph)(Pair(firstParent, secondParent), child)
            assertTrue(child.isFormatCorrect())
        }
    }

    @Test
    fun subTourChunksCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            SubTourChunksCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.isFormatCorrect())
        }
    }

    @Test
    fun votingRecombinationCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            VotingRecombinationCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.isFormatCorrect())
        }
    }
}