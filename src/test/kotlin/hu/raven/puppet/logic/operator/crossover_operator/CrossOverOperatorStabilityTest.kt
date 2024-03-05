package hu.raven.puppet.logic.operator.crossover_operator

import hu.akos.hollo.szabo.collections.asImmutable
import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.model.utility.math.CompleteGraph
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
        val costGraph = CompleteGraph(
            vertices = Array(PROBLEM_SIZE + 1) { GraphVertex(it, Unit) }.asImmutable(),
            edges = Array(PROBLEM_SIZE + 1) { sourceIndex ->
                Array(PROBLEM_SIZE + 1) { targetIndex ->
                    GraphEdge(
                        sourceNodeIndex = sourceIndex,
                        targetNodeIndex = targetIndex,
                        value = Random.nextFloat()
                    )
                }.asImmutable()
            }.asImmutable()
        )
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            HeuristicCrossOver(costGraph) { it }(Pair(firstParent, secondParent), child)
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
        val costGraph = CompleteGraph(
            vertices = Array(PROBLEM_SIZE + 1) {
                GraphVertex(
                    index = it,
                    value = Unit
                )
            }.asImmutable(),
            edges = Array(PROBLEM_SIZE + 1) { sourceNodeIndex ->
                Array(PROBLEM_SIZE + 1) { targetNodeIndex ->
                    GraphEdge(
                        sourceNodeIndex = sourceNodeIndex,
                        targetNodeIndex = targetNodeIndex,
                        value = (Random.nextInt())
                    )
                }.asImmutable()
            }.asImmutable()
        )

        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            SortedMatchCrossOver(costGraph, Int::toFloat)(Pair(firstParent, secondParent), child)
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