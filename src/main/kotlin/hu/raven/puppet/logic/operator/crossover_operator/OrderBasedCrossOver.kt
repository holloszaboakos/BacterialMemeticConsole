package hu.raven.puppet.logic.operator.crossover_operator

import hu.akos.hollo.szabo.math.Permutation
import kotlin.random.Random

//select positions by 50% chance
//copy to child from primary
//fill the rest from secondary
data object OrderBasedCrossOver : CrossOverOperator<Permutation> {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        childPermutation.clear()
        //copy second parent
        val seconderCopy = parentPermutations.second.clone()

        //for each child position
        childPermutation.indices.forEach { valueIndex ->

            //by a fifty percent chance
            if (Random.nextBoolean()) {
                //select value of primary parent in same position
                val selectedValue = parentPermutations.first[valueIndex]
                //remove value from copy of seconder
                seconderCopy.deleteValue(selectedValue)
                //copy value to child
                childPermutation[valueIndex] = selectedValue
            }
        }

        val remainingValuesInSeconderOrder = seconderCopy.filter { it != -1 }.toTypedArray()

        var counter = -1
        //fill missing places of child
        childPermutation.forEachEmptyIndex { index ->
            counter++
            childPermutation[index] = remainingValuesInSeconderOrder[counter]
        }
    }
}