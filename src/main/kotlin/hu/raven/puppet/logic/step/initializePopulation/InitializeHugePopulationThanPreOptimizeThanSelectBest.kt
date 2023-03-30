package hu.raven.puppet.logic.step.initializePopulation

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.bacterialmutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.utility.extention.sum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class InitializeHugePopulationThanPreOptimizeThanSelectBest<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<S, C>,
    private val mutationOperator: MutationOnSpecimen<S, C>,
    private val statistics: BacterialAlgorithmStatistics,
) : InitializePopulation<S, C>() {


    override fun invoke() {
        algorithmState.run {
            val sizeOfPermutation = task.costGraph.objectives.size + task.transportUnits.size - 1
            val basePermutation = IntArray(sizeOfPermutation) { it }

            population = createPopulation()

            population.forEachIndexed { instanceIndex, instance ->
                val step = instanceIndex % (sizeOfPermutation - 1) + 1
                if (step == 1) {
                    basePermutation.shuffle()
                }

                val newContains = BooleanArray(sizeOfPermutation) { false }
                val newPermutation = IntArray(sizeOfPermutation) { -1 }
                var baseIndex = step
                for (newIndex in 0 until sizeOfPermutation) {
                    if (newContains[basePermutation[baseIndex]])
                        baseIndex = (baseIndex + 1) % sizeOfPermutation
                    newPermutation[newIndex] = basePermutation[baseIndex]
                    newContains[basePermutation[baseIndex]] = true
                    baseIndex = (baseIndex + step) % sizeOfPermutation
                }

                val breakPoints = newPermutation
                    .mapIndexed { index, value ->
                        if (value < task.costGraph.objectives.size)
                            -1
                        else
                            index
                    }
                    .filter { it != -1 }
                    .toMutableList()

                breakPoints.add(0, -1)
                breakPoints.add(sizeOfPermutation)
                instance.setData(List(breakPoints.size - 1) {
                    newPermutation.slice((breakPoints[it] + 1) until breakPoints[it + 1]).toIntArray()
                })
                instance.iteration = 0
                instance.inUse = true
                instance.cost = null
            }

            val bestImprovements =
                runBlocking {
                    bacterialMutateEach()
                }

            population =
                    //population
                    //.onEach { calculateCostOf(it) }
                    //.sortedBy { it.cost }
                bestImprovements
                    .map { population[it.first] }
                    .slice(0 until parameters.sizeOfPopulation)
                    .mapIndexed { index, s -> subSolutionFactory.produce(index, s.getData().toTypedArray()) }
                    .toMutableList()
        }
    }

    private suspend fun bacterialMutateEach() = withContext(Dispatchers.Default) {
        val bestImprovement: List<Pair<Int, StepEfficiencyData>>
        algorithmState.population
            .map { specimen ->
                async {
                    mutationOperator(specimen)
                }
            }
            .mapIndexed { index, job ->
                val statistics = job.await()
                println(index)
                statistics
            }
            .also { it ->
                bestImprovement =
                    it.mapIndexed { index, stat -> Pair(index, stat) }
                        .sortedBy { it.second.improvementPercentagePerBudget }
            }
            .sum()
            .also {
                synchronized(statistics) {
                    statistics.mutationImprovement = it
                }
            }
        bestImprovement
    }


    private fun createPopulation(): MutableList<S> {
        return if (algorithmState.task.costGraph.objectives.size != 1)
            ArrayList(List((algorithmState.task.costGraph.objectives.size + algorithmState.task.transportUnits.size - 1)) { specimenIndex ->
                subSolutionFactory.produce(
                    specimenIndex,
                    Array(algorithmState.task.transportUnits.size) { index ->
                        if (index == 0)
                            IntArray(algorithmState.task.costGraph.objectives.size) { it }
                        else
                            intArrayOf()
                    }
                )
            })
        else
            arrayListOf(
                subSolutionFactory.produce(
                    0,
                    arrayOf(IntArray(algorithmState.task.costGraph.objectives.size) { it })
                )
            )
    }
}