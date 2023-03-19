package hu.raven.puppet.modules

import hu.raven.puppet.logic.logging.CSVLogger
import hu.raven.puppet.logic.step.bacterialmutation.BacterialMutation
import hu.raven.puppet.logic.step.bacterialmutation.BacterialMutationOnBestAndLuckyByShuffling
import hu.raven.puppet.logic.step.bacterialmutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.logic.step.bacterialmutationonspecimen.MutationWithElitistSelection
import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.bacterialmutationoperator.EdgeBuilderHeuristicOnContinuousSegment
import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.step.crossoveroperator.HeuristicCrossOver
import hu.raven.puppet.logic.step.genetransfer.GeneTransferByTournament
import hu.raven.puppet.logic.step.genetransferoperator.GeneTransferByCrossOver
import hu.raven.puppet.logic.step.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.logic.step.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.step.initialize.InitializeBacterialAlgorithm
import hu.raven.puppet.logic.step.iterationofevolutionary.BacterialIteration
import hu.raven.puppet.logic.step.iterationofevolutionary.EvolutionaryIteration
import hu.raven.puppet.logic.step.selectsegment.SelectContinuesSegmentWithFullCoverage
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.modules.AlgorithmParameters.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val bacterialModule = module {
    single(named(ITERATION_LIMIT)) { Int.MAX_VALUE }
    single(named(SIZE_OF_POPULATION)) { 50 }

    single(named(MUTATION_PERCENTAGE)) { 0f }
    single(named(CLONE_COUNT)) { 40 }
    single(named(CLONE_SEGMENT_LENGTH)) { 16 }
    single(named(CLONE_CYCLE_COUNT)) { 5 }

    single(named(GENE_TRANSFER_SEGMENT_LENGTH)) { 900 }
    single(named(INJECTION_COUNT)) { 100 }

    single(named(OPTIMISATION_STEP_LIMIT)) { 1000 }

    single<IterativeAlgorithmStateWithMultipleCandidates<OnePartRepresentation<Meter>, *>> {
        IterativeAlgorithmStateWithMultipleCandidates()
    }

    factory<InitializeAlgorithm<*, *>> {
        InitializeBacterialAlgorithm<OnePartRepresentation<Meter>, Meter>()
    }

    factory<EvolutionaryIteration<*, *>> {
        BacterialIteration<OnePartRepresentation<Meter>, Meter>()
    }

    factory<BacterialMutation<*, *>> {
        BacterialMutationOnBestAndLuckyByShuffling<OnePartRepresentation<Meter>, Meter>()
    }
    factory<MutationOnSpecimen<*, *>> {
        MutationWithElitistSelection<OnePartRepresentation<Meter>, Meter>()
    }
    factory<SelectSegment<*, *>> {
        SelectContinuesSegmentWithFullCoverage<OnePartRepresentation<Meter>, Meter>()
    }
    factory<BacterialMutationOperator<*, *>> {
        EdgeBuilderHeuristicOnContinuousSegment<OnePartRepresentation<Meter>, Meter>()
    }
    factory<hu.raven.puppet.logic.step.genetransfer.GeneTransfer<*, *>> {
        GeneTransferByTournament<OnePartRepresentation<Meter>, Meter>()
    }
    factory<GeneTransferOperator<*, *>> {
        GeneTransferByCrossOver<OnePartRepresentation<Meter>, Meter>()
    }
    factory<CrossOverOperator<*, *>> {
        HeuristicCrossOver<OnePartRepresentation<Meter>, Meter>()
    }

    single {
        BacterialAlgorithmStatistics()
    }

    single {
        CSVLogger()
    }
}
