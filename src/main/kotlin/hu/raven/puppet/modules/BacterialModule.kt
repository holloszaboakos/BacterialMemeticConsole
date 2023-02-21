package hu.raven.puppet.modules

import hu.raven.puppet.logic.logging.CSVLogger
import hu.raven.puppet.logic.specimen.DOnePartRepresentation
import hu.raven.puppet.logic.state.EvolutionaryAlgorithmState
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.logic.step.common.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.step.common.initialize.InitializeBacterialAlgorithm
import hu.raven.puppet.logic.step.evolutionary.bacterial.genetransfer.GeneTransfer
import hu.raven.puppet.logic.step.evolutionary.bacterial.genetransfer.GeneTransferByTournament
import hu.raven.puppet.logic.step.evolutionary.bacterial.genetransferoperator.GeneTransferByCrossOver
import hu.raven.puppet.logic.step.evolutionary.bacterial.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.logic.step.evolutionary.bacterial.mutation.BacterialMutation
import hu.raven.puppet.logic.step.evolutionary.bacterial.mutation.BacterialMutationOnBestAndLuckyByShuffling
import hu.raven.puppet.logic.step.evolutionary.bacterial.mutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.logic.step.evolutionary.bacterial.mutationonspecimen.MutationOnSpecimenWithRandomContinuousSegmentAndFullCover
import hu.raven.puppet.logic.step.evolutionary.bacterial.mutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.evolutionary.bacterial.mutationoperator.MutationOperatorWithContinuousSegmentAndEdgeBuilderHeuristics
import hu.raven.puppet.logic.step.evolutionary.bacterial.selectsegment.SelectContinuesSegment
import hu.raven.puppet.logic.step.evolutionary.bacterial.selectsegment.SelectSegment
import hu.raven.puppet.logic.step.evolutionary.common.iteration.BacterialIteration
import hu.raven.puppet.logic.step.evolutionary.common.iteration.EvolutionaryIteration
import hu.raven.puppet.logic.step.evolutionary.genetic.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.step.evolutionary.genetic.crossoveroperator.HeuristicCrossOver
import hu.raven.puppet.model.physics.Meter
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

    single<EvolutionaryAlgorithmState<DOnePartRepresentation<Meter>, *>> {
        EvolutionaryAlgorithmState()
    }

    factory<InitializeAlgorithm<*, *>> {
        InitializeBacterialAlgorithm<DOnePartRepresentation<Meter>, Meter>()
    }

    factory<EvolutionaryIteration<*, *>> {
        BacterialIteration<DOnePartRepresentation<Meter>, Meter>()
    }

    factory<BacterialMutation<*, *>> {
        BacterialMutationOnBestAndLuckyByShuffling<DOnePartRepresentation<Meter>, Meter>()
    }
    factory<MutationOnSpecimen<*, *>> {
        MutationOnSpecimenWithRandomContinuousSegmentAndFullCover<DOnePartRepresentation<Meter>, Meter>()
    }
    factory<BacterialMutationOperator<*, *>> {
        MutationOperatorWithContinuousSegmentAndEdgeBuilderHeuristics<DOnePartRepresentation<Meter>, Meter>()
    }
    factory<GeneTransfer<*, *>> {
        GeneTransferByTournament<DOnePartRepresentation<Meter>, Meter>()
    }
    factory<GeneTransferOperator<*, *>> {
        GeneTransferByCrossOver<DOnePartRepresentation<Meter>, Meter>()
    }
    factory<CrossOverOperator<*, *>> {
        HeuristicCrossOver<DOnePartRepresentation<Meter>, Meter>()
    }
    factory<SelectSegment<*, *>> {
        SelectContinuesSegment<DOnePartRepresentation<Meter>, Meter>()
    }

    single {
        BacterialAlgorithmStatistics()
    }

    single {
        CSVLogger()
    }
}
