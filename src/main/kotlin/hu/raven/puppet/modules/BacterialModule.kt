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
import hu.raven.puppet.logic.step.evolutionary.bacterial.mutationonspecimen.MutationOnSpecimenWithRandomContinuousSegmentAndFullCoverAndCloneWithInvertion
import hu.raven.puppet.logic.step.evolutionary.bacterial.mutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.evolutionary.bacterial.mutationoperator.MutationOperatorWithContinuousSegmentAndEdgeBuilderHeuristics
import hu.raven.puppet.logic.step.evolutionary.bacterial.selectsegment.SelectContinuesSegment
import hu.raven.puppet.logic.step.evolutionary.bacterial.selectsegment.SelectSegment
import hu.raven.puppet.logic.step.evolutionary.common.iteration.BacterialIteration
import hu.raven.puppet.logic.step.evolutionary.common.iteration.EvolutionaryIteration
import hu.raven.puppet.logic.step.evolutionary.genetic.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.step.evolutionary.genetic.crossoveroperator.HeuristicCrossOver
import hu.raven.puppet.modules.AlgorithmParameters.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val bacterialModule = module {
    single(named(ITERATION_LIMIT)) { Int.MAX_VALUE }
    single(named(SIZE_OF_POPULATION)) { 100 }

    single(named(MUTATION_PERCENTAGE)) { 0f }
    single(named(CLONE_COUNT)) { 10 }
    single(named(CLONE_SEGMENT_LENGTH)) { 32 }
    single(named(CLONE_CYCLE_COUNT)) { 100 }

    single(named(GENE_TRANSFER_SEGMENT_LENGTH)) { 900 }
    single(named(INJECTION_COUNT)) { 100 }

    single<EvolutionaryAlgorithmState<DOnePartRepresentation>> {
        EvolutionaryAlgorithmState()
    }

    factory<InitializeAlgorithm<*>> {
        InitializeBacterialAlgorithm<DOnePartRepresentation>()
    }

    factory<EvolutionaryIteration<*>> {
        BacterialIteration<DOnePartRepresentation>()
    }

    factory<BacterialMutation<*>> {
        BacterialMutationOnBestAndLuckyByShuffling<DOnePartRepresentation>(
            mutationPercentage = get(named(MUTATION_PERCENTAGE))
        )
    }
    factory<MutationOnSpecimen<*>> {
        MutationOnSpecimenWithRandomContinuousSegmentAndFullCoverAndCloneWithInvertion<DOnePartRepresentation>()
    }
    factory<BacterialMutationOperator<*>> {
        MutationOperatorWithContinuousSegmentAndEdgeBuilderHeuristics<DOnePartRepresentation>()
    }
    factory<GeneTransfer<*>> {
        GeneTransferByTournament<DOnePartRepresentation>()
    }
    factory<GeneTransferOperator<*>> {
        GeneTransferByCrossOver<DOnePartRepresentation>()
    }
    factory<CrossOverOperator<*>> {
        HeuristicCrossOver<DOnePartRepresentation>()
    }
    factory<SelectSegment<*>> {
        SelectContinuesSegment<DOnePartRepresentation>()
    }

    single {
        BacterialAlgorithmStatistics()
    }

    single {
        CSVLogger()
    }
}
