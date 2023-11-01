package hu.raven.puppet.utility.extention

fun IntRange.selectRandomPositions(count: Int) =
    shuffled()
        .slice(0 ..<count)
        .sorted()
        .toIntArray()