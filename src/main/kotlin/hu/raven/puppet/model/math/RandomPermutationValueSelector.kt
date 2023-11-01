package hu.raven.puppet.model.math

class RandomPermutationValueSelector(
    val size: Int
) {
    private val randomPermutation = (0 ..<size).shuffled().toIntArray()
    private var lastIndex = 0

    fun getNextExcludingIf(doExclude: (Int) -> Boolean): Int? {
        for (index in lastIndex ..<size) {
            if (!doExclude(randomPermutation[index])) {
                lastIndex = index + 1
                return randomPermutation[index]
            }
        }

        lastIndex = size
        return null
    }
}