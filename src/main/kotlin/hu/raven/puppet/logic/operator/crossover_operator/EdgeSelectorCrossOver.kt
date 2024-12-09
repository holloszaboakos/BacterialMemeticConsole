package hu.raven.puppet.logic.operator.crossover_operator

import hu.akos.hollo.szabo.math.Permutation
import kotlin.random.Random

//select random positions from sequence representation of primary parent
//try adding positions from seconder parent
//connect rest of segments randomly
//support: track built segments for cycle detection
//O(n^2)
data object EdgeSelectorCrossOver : CrossOverOperator<Permutation> {
    override fun invoke(parentPermutations: Pair<Permutation, Permutation>, childPermutation: Permutation) {
        childPermutation.clear()

        val segments = mutableListOf<Pair<Int, Int>>()
        val selectedEdges = IntArray(childPermutation.size + 1) { -1 }

        for (index in 0..childPermutation.size) {
            if (Random.nextBoolean()) {
                selectedEdges[index] = parentPermutations.first.after(index)
            }
        }

        selectedEdges.forEachIndexed { from, to ->
            if (to == -1) return@forEachIndexed


            val followed = segments.firstOrNull { it.second == from }
            val preceded = segments.firstOrNull { it.first == to }

            when {
                followed == null && preceded == null -> segments.add(Pair(from, to))
                followed == null && preceded != null -> {
                    segments.remove(preceded)
                    segments.add(Pair(from, preceded.second))
                }

                followed != null && preceded == null -> {
                    segments.remove(followed)
                    segments.add(Pair(followed.first, to))
                }

                followed != null && preceded != null -> {
                    segments.remove(preceded)
                    segments.remove(followed)
                    segments.add(Pair(followed.first, preceded.second))
                }
            }
        }

        for (index in 0..childPermutation.size) {
            if (selectedEdges[index] != -1) continue

            val edge = Pair(index, parentPermutations.second.after(index))

            if (segments.any { it.first == edge.second && it.second == edge.first }) {
                continue
            }

            if (selectedEdges.contains(edge.second)) {
                continue
            }

            val followed = segments.firstOrNull { it.second == edge.first }
            val preceded = segments.firstOrNull { it.first == edge.second }

            when {
                followed == null && preceded == null -> segments.add(edge)
                followed == null && preceded != null -> {
                    segments.remove(preceded)
                    segments.add(Pair(edge.first, preceded.second))
                }

                followed != null && preceded == null -> {
                    segments.remove(followed)
                    segments.add(Pair(followed.first, edge.second))
                }

                followed != null && preceded != null -> {
                    segments.remove(preceded)
                    segments.remove(followed)
                    segments.add(Pair(followed.first, preceded.second))
                }
            }

            selectedEdges[edge.first] = edge.second
        }

        for (index in 0..childPermutation.size) {
            if (selectedEdges[index] != -1) continue

            val parallelFirst = segments.firstOrNull { it.second == index }?.first

            val availableValues = (0..childPermutation.size)
                .filter { it != index && it != parallelFirst && !selectedEdges.contains(it) }

            if (availableValues.isEmpty()) break

            val edge = try {
                Pair(index, availableValues.random())
            } catch (e: NoSuchElementException) {
                throw e
            }

            val followed = segments.firstOrNull { it.second == edge.first }
            val preceded = segments.firstOrNull { it.first == edge.second }

            when {
                followed == null && preceded == null -> segments.add(edge)
                followed == null && preceded != null -> {
                    segments.remove(preceded)
                    segments.add(Pair(edge.first, preceded.second))
                }

                followed != null && preceded == null -> {
                    segments.remove(followed)
                    segments.add(Pair(followed.first, edge.second))
                }

                followed != null && preceded != null -> {
                    segments.remove(preceded)
                    segments.remove(followed)
                    segments.add(Pair(followed.first, preceded.second))
                }
            }

            selectedEdges[edge.first] = edge.second
        }

        selectedEdges[segments.first().second] = segments.first().first

        var currentValue = selectedEdges[childPermutation.size]
        for (index in 0..<childPermutation.size) {
            childPermutation[index] = currentValue
            currentValue = selectedEdges[currentValue]
        }

        if (!childPermutation.isFormatCorrect()) {
            throw Exception("Wrong child format!")
        }
    }
}