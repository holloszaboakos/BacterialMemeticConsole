package hu.raven.puppet.utility.extention

object FloatSumExtensions{

    fun FloatArray.sumClever(): Float = asIterable().sumClever()

    fun Array<Float>.sumClever(): Float = asIterable().sumClever()

    fun Iterable<Float>.sumClever(): Float {
        when (count()) {
            0 -> return 0f
            1 -> return first()
            2 -> return sum()
        }

        val sorted = sorted().toMutableList()

        while (sorted.size >= 2) {
            val sum = sorted[0] + sorted[1]
            sorted.removeAt(1)
            sorted.removeAt(0)

            val index = sorted.binarySearchIndexOfFirstGreaterThan(sum)

            if (index == -1) {
                sorted.add(sum)
            } else {
                sorted.add(index, sum)
            }
        }
        return sorted.first()
    }

    private fun List<Float>.binarySearchIndexOfFirstGreaterThan(value: Float): Int {
        if (isEmpty()) return -1

        var lowerLimit = 0
        var upperLimit = lastIndex

        while (lowerLimit != upperLimit) {
            var index = (lowerLimit + upperLimit) / 2

            if (get(index) < value) {
                lowerLimit = index

                if (upperLimit == lowerLimit + 1) {
                    index = upperLimit
                } else {
                    continue
                }
            }

            if (index == 0) return 0

            if (get(index - 1) < value) return index

            upperLimit = index
        }

        return -1
    }

}

