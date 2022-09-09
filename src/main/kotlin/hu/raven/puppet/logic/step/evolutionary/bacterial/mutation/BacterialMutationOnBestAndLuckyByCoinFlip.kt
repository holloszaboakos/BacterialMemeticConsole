package hu.raven.puppet.logic.step.evolutionary.bacterial.mutation

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.logic.step.evolutionary.bacterial.mutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.utility.extention.sum
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.random.Random

class BacterialMutationOnBestAndLuckyByCoinFlip<S : ISpecimenRepresentation>(
    private val mutationPercentage: Float
) : BacterialMutation<S>() {

    private val statistics: BacterialAlgorithmStatistics by inject()
    private val mutationOnSpecimen: MutationOnSpecimen<S> by inject()

    override suspend fun invoke(): Unit = withContext(Dispatchers.Default) {
        algorithmState.run {
            population.mapIndexed { index, specimen ->
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