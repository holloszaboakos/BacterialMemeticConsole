package hu.raven.puppet.logic.modules

import hu.raven.puppet.logic.AAlgorithm4VRP
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
import hu.raven.puppet.logic.specimen.DOnePartRepresentation
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

val bacterialModule = module {

    single<BacterialAlgorithm<DOnePartRepresentation>> {
        BacterialAlgorithm(
            iterationLimit = Int.MAX_VALUE,
            sizeOfPopulation = 100,

            cloneCount = 10,
            cloneSegmentLength = 16,
            cloneCycleCount = 100,

            geneTransferSegmentLength = 900,
            injectionCount = 100
        )
    } withOptions {
        bind<SEvolutionaryAlgorithm<*>>()
        bind<AAlgorithm4VRP<*>>()
    }

    factory<InitializeAlgorithm<*>> {
        InitializeBacterialAlgorithm<DOnePartRepresentation>(get())
    }

    factory<EvolutionaryIteration<*>> {
        BacterialIteration<DOnePartRepresentation>(get())
    }

    factory<BacterialMutation<*>> {
        BacterialMutationOnAllAndFullCoverRandomOrder<DOnePartRepresentation>(
            algorithm = get(),
            mutationPercentage = 0f
        )
    }
    factory<BacterialMutationOperator<*>> {
        MutationOperatorWithContinuousSegmentAndEdgeBuilderHeuristics<DOnePartRepresentation>(
            algorithm = get()
        )
    }
    factory<GeneTransfer<*>> {
        GeneTransferByTournament<DOnePartRepresentation>(
            algorithm = get()
        )
    }
    factory<GeneTransferOperator<*>> {
        HeuristicGeneTransfer<DOnePartRepresentation>(
            algorithm = get()
        )
    }
    factory<SelectSegment<*>> {
        SelectContinuesSegment<DOnePartRepresentation>(
            algorithm = get()
        )
    }
}
