package hu.raven.puppet.logic

import hu.raven.puppet.logic.common.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.common.initialize.InitializeBacterialAlgorithm
import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.evolutionary.bacterial.genetransfer.GeneTransfer
import hu.raven.puppet.logic.evolutionary.bacterial.genetransfer.GeneTransferByTournament
import hu.raven.puppet.logic.evolutionary.bacterial.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.logic.evolutionary.bacterial.genetransferoperator.HeuristicGeneTransfer
import hu.raven.puppet.logic.evolutionary.bacterial.mutation.BacterialMutation
import hu.raven.puppet.logic.evolutionary.bacterial.mutation.BacterialMutationOnAllAndFullCoverRandomOrder
import hu.raven.puppet.logic.evolutionary.bacterial.mutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.evolutionary.bacterial.mutationoperator.MutationOperatorWithContinuousSegmentAndEdgeBuilderHeuristics
import hu.raven.puppet.logic.evolutionary.bacterial.selectsegment.SelectContinuesSegment
import hu.raven.puppet.logic.evolutionary.bacterial.selectsegment.SelectSegment
import hu.raven.puppet.logic.evolutionary.common.iteration.BacterialIteration
import hu.raven.puppet.logic.evolutionary.common.iteration.EvolutionaryIteration
import hu.raven.puppet.logic.evolutionary.setup.BacterialAlgorithmSetup
import hu.raven.puppet.logic.specimen.DOnePartRepresentation
import org.koin.dsl.module

val bacterialModule = module {
    factory<InitializeAlgorithm>() {
        InitializeBacterialAlgorithm()
    }

    factory<EvolutionaryIteration> {
        BacterialIteration()
    }

    factory<BacterialMutation> {
        BacterialMutationOnAllAndFullCoverRandomOrder(
            mutationPercentage = 0f
        )
    }
    factory<BacterialMutationOperator> {
        MutationOperatorWithContinuousSegmentAndEdgeBuilderHeuristics()
    }
    factory<GeneTransfer> { GeneTransferByTournament() }
    factory<GeneTransferOperator> { HeuristicGeneTransfer() }
    factory<SelectSegment> { SelectContinuesSegment() }

    factory {
        BacterialAlgorithmSetup(
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
            get(),
            get()
        )
    }

    single<SEvolutionaryAlgorithm<DOnePartRepresentation>> {
        BacterialAlgorithm(
            iterationLimit = Int.MAX_VALUE,
            sizeOfPopulation = 100,

            cloneCount = 10,
            cloneSegmentLength = 16,
            cloneCycleCount = 100,

            geneTransferSegmentLength = 900,
            injectionCount = 100
        )
    }
}
