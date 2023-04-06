package hu.raven.puppet.logic.step.bacterialmutation

import hu.raven.puppet.logic.step.bacterialmutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.utility.extention.sum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.random.Random

class BacterialMutationOnBestAndLuckyByShuffling<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: BacterialMutationParameterProvider<C>,
    override val statistics: BacterialAlgorithmStatistics,
    override val mutationOnSpecimen: MutationOnSpecimen<C>
) : BacterialMutation<C>() {

    override suspend fun invoke(): Unit = withContext(Dispatchers.Default) {
        algorithmState.run {
            val selectedCount = ((population.size - 1) * parameters.mutationPercentage).toInt()

            val populationRandomized = population.slice(1 until population.size)
                .shuffled()
                .slice(0 until selectedCount)
                .toMutableList()
                .apply { add(0, population.first()) }

            populationRandomized.mapIndexed { index, specimen ->
                async {
                    if (index != 0 && Random.nextFloat() > parameters.mutationPercentage) {
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