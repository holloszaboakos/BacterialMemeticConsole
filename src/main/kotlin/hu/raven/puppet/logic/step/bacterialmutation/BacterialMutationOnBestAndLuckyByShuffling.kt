package hu.raven.puppet.logic.step.bacterialmutation

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.bacterialmutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.utility.extention.sum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.random.Random

class BacterialMutationOnBestAndLuckyByShuffling<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val taskHolder: VRPTaskHolder,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val sizeOfPopulation: Int,
    override val iterationLimit: Int,
    override val geneCount: Int,
    override val mutationPercentage: Float,
    override val statistics: BacterialAlgorithmStatistics,
    override val mutationOnSpecimen: MutationOnSpecimen<S, C>
) :
    BacterialMutation<S, C>() {


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