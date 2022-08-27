package hu.raven.puppet.logic

import hu.raven.puppet.logic.common.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.common.initialize.InitializeBacterialAlgorithm
import hu.raven.puppet.logic.common.initialize.InitializeLocalSearchAlgorithm
import hu.raven.puppet.logic.common.logging.DoubleLogger
import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCostOfVRPSolutionWithoutCapacity
import hu.raven.puppet.logic.common.steps.calculatecostofedge.CalculateCostOfEdge
import hu.raven.puppet.logic.common.steps.calculatecostofobjective.CalculateCostOfObjective
import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.evolutionary.bacterial.genetransfer.GeneTransfer
import hu.raven.puppet.logic.evolutionary.bacterial.genetransfer.GeneTransferByTournament
import hu.raven.puppet.logic.evolutionary.bacterial.genetransfer.GeneTransferFromBetterToWorse
import hu.raven.puppet.logic.evolutionary.bacterial.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.logic.evolutionary.bacterial.genetransferoperator.HeuristicGeneTransfer
import hu.raven.puppet.logic.evolutionary.bacterial.genetransferoperator.SegmentInjectionGeneTransfer
import hu.raven.puppet.logic.evolutionary.bacterial.mutation.BacterialMutation
import hu.raven.puppet.logic.evolutionary.bacterial.mutation.BacterialMutationOnAllAndFullCoverRandomOrder
import hu.raven.puppet.logic.evolutionary.bacterial.mutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.evolutionary.bacterial.mutationoperator.MutationOperatorWithContinuesSegment
import hu.raven.puppet.logic.evolutionary.bacterial.mutationoperator.MutationOperatorWithContinuousSegmentAndEdgeBuilderHeuristics
import hu.raven.puppet.logic.evolutionary.bacterial.mutationoperator.MutationOperatorWithContinuousSegmentAndHeuristicApproach
import hu.raven.puppet.logic.evolutionary.bacterial.selectsegment.SelectContinuesSegment
import hu.raven.puppet.logic.evolutionary.bacterial.selectsegment.SelectSegment
import hu.raven.puppet.logic.evolutionary.common.OrderPopulationByCost
import hu.raven.puppet.logic.evolutionary.common.boost.*
import hu.raven.puppet.logic.evolutionary.common.boostoperator.BoostOperator
import hu.raven.puppet.logic.evolutionary.common.boostoperator.Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit
import hu.raven.puppet.logic.evolutionary.common.diversity.Diversity
import hu.raven.puppet.logic.evolutionary.common.diversity.DiversityByInnerDistanceAndSequence
import hu.raven.puppet.logic.evolutionary.common.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.evolutionary.common.initializePopulation.InitializePopulationByModuloStepper
import hu.raven.puppet.logic.evolutionary.common.iteration.BacterialIteration
import hu.raven.puppet.logic.evolutionary.common.iteration.EvolutionaryIteration
import hu.raven.puppet.logic.evolutionary.genetic.CrossOvers
import hu.raven.puppet.logic.evolutionary.genetic.SelectSurvivors
import hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator.HeuristicCrossOver
import hu.raven.puppet.logic.evolutionary.genetic.mutatechildren.MutateChildren
import hu.raven.puppet.logic.evolutionary.genetic.mutatechildren.MutateChildrenBySwap
import hu.raven.puppet.logic.evolutionary.setup.BacterialAlgorithmSetup
import hu.raven.puppet.logic.evolutionary.setup.GeneticAlgorithmSetup
import hu.raven.puppet.logic.localsearch.Opt2
import hu.raven.puppet.logic.localsearch.SLocalSearch
import hu.raven.puppet.logic.localsearch.initialize.InitializeByRandom
import hu.raven.puppet.logic.localsearch.initialize.InitializeLocalSearch
import hu.raven.puppet.logic.localsearch.iteration.LocalSearchIteration
import hu.raven.puppet.logic.localsearch.iteration.Opt2Iteration
import hu.raven.puppet.logic.specimen.DOnePartRepresentation
import hu.raven.puppet.logic.specimen.factory.OnePartRepresentationFactory
import hu.raven.puppet.logic.specimen.factory.SSpecimenRepresentationFactory
import hu.raven.puppet.logic.statistics.Statistics
import hu.raven.puppet.model.inner.setup.LocalSearchSetup
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

val logicModule = module {
    single {
        DoubleLogger(File(""))
    }

    factory<Diversity> {
        DiversityByInnerDistanceAndSequence()
    }

    factory<SSpecimenRepresentationFactory<*>> {
        OnePartRepresentationFactory()
        //OTwoPartRepresentationFactory()
    }

    factory<InitializeAlgorithm>(named("evolutionary")) {
        InitializeBacterialAlgorithm()
        //InitializeGeneticAlgorithm()
    }
    factory<InitializePopulation> {
        InitializePopulationByModuloStepper()
    }
    factory<EvolutionaryIteration> {
        BacterialIteration()
        //GeneticIteration()
    }

    factory { OrderPopulationByCost() }
    factory<CalculateCost> { CalculateCostOfVRPSolutionWithoutCapacity() }
    factory { CalculateCostOfEdge() }
    factory { CalculateCostOfObjective() }

    factory<Boost> {
        BoostOnBestAndLucky(4)
    }
    factory<BoostOperator> {
        Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit(2000)
    }

    factory { SelectSurvivors() }
    factory { CrossOvers() }
    factory<CrossOverOperator> { HeuristicCrossOver() }
    factory<MutateChildren> { MutateChildrenBySwap() }

    factory<BacterialMutation> {
        BacterialMutationOnAllAndFullCoverRandomOrder(
            mutationPercentage = 0.02f
        )
    }
    factory<BacterialMutationOperator> {
        MutationOperatorWithContinuousSegmentAndEdgeBuilderHeuristics()
    }
    factory<GeneTransfer> { GeneTransferByTournament() }
    factory<GeneTransferOperator> { HeuristicGeneTransfer() }
    factory<SelectSegment> { SelectContinuesSegment() }

    single { Statistics() }

    factory {
        GeneticAlgorithmSetup(
            get(named("evolutionary")),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }

    factory {
        BacterialAlgorithmSetup(
            get(named("evolutionary")),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }

    factory<InitializeAlgorithm>(named("default")) {
        InitializeLocalSearchAlgorithm()
    }

    factory<InitializeLocalSearch> {
        InitializeByRandom()
    }

    factory<LocalSearchIteration> {
        Opt2Iteration()
    }

    factory {
        LocalSearchSetup(
            get(named("default")),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }

    single<SLocalSearch<DOnePartRepresentation>> {
        Opt2()
    }

    single<SEvolutionaryAlgorithm<DOnePartRepresentation>> {

        BacterialAlgorithm(
            iterationLimit = Int.MAX_VALUE,
            sizeOfPopulation = 100,

            cloneCount = 10,
            cloneSegmentLength = 50,
            cloneCycleCount = 36,

            geneTransferSegmentLength = 900,
            injectionCount = 400
        )
        //GeneticAlgorithm(26000,8000)

    }

    single {
        AlgorithmManagerService()
    }
}