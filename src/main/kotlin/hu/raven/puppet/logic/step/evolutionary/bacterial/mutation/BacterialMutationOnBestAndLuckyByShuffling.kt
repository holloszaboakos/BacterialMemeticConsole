package hu.raven.puppet.logic.step.evolutionary.bacterial.mutation

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.logic.step.evolutionary.bacterial.mutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.utility.extention.sum
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.random.Random

class BacterialMutationOnBestAndLuckyByShuffling<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> :
    BacterialMutation<S, C>() {

    private val mutationPercentage: Float by inject(AlgorithmParameters.MUTATION_PERCENTAGE)
    private val statistics: BacterialAlgorithmStatistics by inject()
    private val mutationOnSpecimen: MutationOnSpecimen<S, C> by inject()

    override suspend fun invoke(
    ): Unit = withContext(Dispatchers.Default) {
        algorithmState.run {
            val selectedCount = ((population.size - 1) * mutationPercentage).toInt()

            val populationRandomized = population.slice(1 until population.size)
                .shuffled()
                .slice(0 until selectedCount)
                .toMutableList()
                .apply { add(0, population.first()) }

            populationRandomized.mapIndexed { index, specimen ->
                async {
                    if (index != 0 && Random.nextFloat() > mutationPercentage) {
                        return@async null
                    }

                    val improvement = mutationOnSpecimen(specimen)

                    if (index == 0) {
                        synchronized(statistics) {
                            statistics.mutationOnBestImprovement =
                                improvement.let {
                                    StepEfficiencyData(
                                        spentTime = it.spentTime,
                                        spentBudget = it.spentBudget,
                                        improvementCountPerRun = it.improvementCountPerRun,
                                        improvementPercentagePerBudget = it.improvementPercentagePerBudget,
                                    )
                                }
                        }
                    }

                    return@async improvement
                }
            }
                .mapNotNull { it.await() }
                .sum()
                .also {
                    synchronized(statistics) {
                        statistics.mutationImprovement = it
                    }
                }
        }
    }
}