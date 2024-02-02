package hu.raven.puppet.logic.operator.crossover_operator

import hu.akos.hollo.szabo.collections.asImmutable
import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.physics.Gram
import hu.akos.hollo.szabo.physics.Meter
import hu.akos.hollo.szabo.physics.Second
import hu.akos.hollo.szabo.physics.CubicMeter
import hu.raven.puppet.model.task.CostGraph
import hu.raven.puppet.model.task.CostGraphEdge
import hu.raven.puppet.model.task.CostGraphVertex
import hu.raven.puppet.model.task.Gps
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
            assertTrue(child.checkFormat())
        }
    }

    @Test
    fun alternatingPositionCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            AlternatingPositionCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.checkFormat())
        }
    }

    @Test
    fun cycleCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            CycleCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.checkFormat())
        }
    }

    @Test
    fun distancePreservingCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            DistancePreservingCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.checkFormat())
        }
    }

    @Test
    fun geneticEdgeRecombinationCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            GeneticEdgeRecombinationCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.checkFormat())
        }
    }

    @Test
    fun heuristicCrossOver() {
        val costGraph = CostGraph(
            center = Gps(Random.nextFloat(), Random.nextFloat()),
            objectives = Array(PROBLEM_SIZE) {
                CostGraphVertex(
                    location = Gps(Random.nextFloat(), Random.nextFloat()),
                    time = Second(Random.nextFloat()),
                    volume = CubicMeter(Random.nextFloat()),
                    weight = Gram(Random.nextFloat())
                )
            }.asImmutable(),
            edgesBetween = Array(PROBLEM_SIZE) {
                Array(PROBLEM_SIZE) {
                    CostGraphEdge(length = Meter(Random.nextFloat()))
                }.asImmutable()
            }.asImmutable(),
            edgesFromCenter = Array(PROBLEM_SIZE) {
                CostGraphEdge(length = Meter(Random.nextFloat()))
            }.asImmutable(),
            edgesToCenter = Array(PROBLEM_SIZE) {
                CostGraphEdge(length = Meter(Random.nextFloat()))
            }.asImmutable(),
        )
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            HeuristicCrossOver(costGraph)(Pair(firstParent, secondParent), child)
            assertTrue(child.checkFormat())
        }
    }

    @Test
    fun maximalPreservationCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            MaximalPreservationCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.checkFormat())
        }
    }

    @Test
    fun orderBasedCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            OrderBasedCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.checkFormat())
        }
    }

    @Test
    fun orderCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            OrderCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.checkFormat())
        }
    }

    @Test
    fun partiallyMatchedCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            PartiallyMatchedCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.checkFormat())
        }
    }

    @Test
    fun positionBasedCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            PositionBasedCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.checkFormat())
        }
    }

    @Test
    fun sortedMatchCrossOver() {
        val costGraph = CostGraph(
            center = Gps(Random.nextFloat(), Random.nextFloat()),
            objectives = Array(PROBLEM_SIZE) {
                CostGraphVertex(
                    location = Gps(Random.nextFloat(), Random.nextFloat()),
                    time = Second(Random.nextFloat()),
                    volume = CubicMeter(Random.nextFloat()),
                    weight = Gram(Random.nextFloat())
                )
            }.asImmutable(),
            edgesBetween = Array(PROBLEM_SIZE) {
                Array(PROBLEM_SIZE) {
                    CostGraphEdge(length = Meter(Random.nextFloat()))
                }.asImmutable()
            }.asImmutable(),
            edgesFromCenter = Array(PROBLEM_SIZE) {
                CostGraphEdge(length = Meter(Random.nextFloat()))
            }.asImmutable(),
            edgesToCenter = Array(PROBLEM_SIZE) {
                CostGraphEdge(length = Meter(Random.nextFloat()))
            }.asImmutable(),
        )
        
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            SortedMatchCrossOver(costGraph)(Pair(firstParent, secondParent), child)
            assertTrue(child.checkFormat())
        }
    }

    @Test
    fun subTourChunksCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            SubTourChunksCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.checkFormat())
        }
    }

    @Test
    fun votingRecombinationCrossOver() {
        repeat(REPEAT_TIME) {
            val firstParent = Permutation.random(PROBLEM_SIZE)
            val secondParent = Permutation.random(PROBLEM_SIZE)
            val child = Permutation.random(PROBLEM_SIZE)

            VotingRecombinationCrossOver(Pair(firstParent, secondParent), child)
            assertTrue(child.checkFormat())
        }
    }
}