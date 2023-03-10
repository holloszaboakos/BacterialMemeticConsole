package hu.raven.puppet.modules

import hu.raven.puppet.logic.logging.CSVLogger
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.logic.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.logic.step.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.step.initialize.InitializeBacterialAlgorithm
import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.step.crossoveroperator.HeuristicCrossOver
import hu.raven.puppet.logic.step.genetransfer.GeneTransferByTournament
import hu.raven.puppet.logic.step.genetransferoperator.GeneTransferByCrossOver
import hu.raven.puppet.logic.step.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.logic.step.iterationofevolutionary.BacterialIteration
import hu.raven.puppet.logic.step.iterationofevolutionary.EvolutionaryIteration
import hu.raven.puppet.logic.step.mutationofbacterial.BacterialMutationOnBestAndLuckyByShuffling
import hu.raven.puppet.logic.step.mutationofbacterial.BacterialMutation
import hu.raven.puppet.logic.step.mutationonspecimen.MutationOnSpecimenWithRandomContinuousSegmentAndFullCover
import hu.raven.puppet.logic.step.mutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.logic.step.mutationoperator.MutationOperatorWithContinuousSegmentAndEdgeBuilderHeuristics
import hu.raven.puppet.logic.step.mutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.selectsegment.SelectContinuesSegment
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
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
        MutationOnSpecimenWithRandomContinuousSegmentAndFullCover<OnePartRepresentation<Meter>, Meter>()
    }
    factory<BacterialMutationOperator<*, *>> {
        MutationOperatorWithContinuousSegmentAndEdgeBuilderHeuristics<OnePartRepresentation<Meter>, Meter>()
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
    factory<SelectSegment<*, *>> {
        SelectContinuesSegment<OnePartRepresentation<Meter>, Meter>()
    }

    single {
        BacterialAlgorithmStatistics()
    }

    single {
        CSVLogger()
    }
}
