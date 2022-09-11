package hu.raven.puppet.modules

import hu.raven.puppet.logic.logging.CSVLogger
import hu.raven.puppet.logic.specimen.DOnePartRepresentation
import hu.raven.puppet.logic.specimen.factory.OnePartRepresentationFactory
import hu.raven.puppet.logic.specimen.factory.SSpecimenRepresentationFactory
import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.common.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.common.calculatecost.CalculateCostOfVRPSolutionWithoutCapacity
import hu.raven.puppet.logic.step.common.calculatecostofedge.CalculateCostOfEdge
import hu.raven.puppet.logic.step.common.calculatecostofobjective.CalculateCostOfObjective
import hu.raven.puppet.logic.step.evolutionary.common.OrderPopulationByCost
import hu.raven.puppet.logic.step.evolutionary.common.boost.Boost
import hu.raven.puppet.logic.step.evolutionary.common.boost.BoostOnBestLazy
import hu.raven.puppet.logic.step.evolutionary.common.boostoperator.BoostOperator
import hu.raven.puppet.logic.step.evolutionary.common.boostoperator.Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit
import hu.raven.puppet.logic.step.evolutionary.common.diversity.Diversity
import hu.raven.puppet.logic.step.evolutionary.common.diversity.DiversityByInnerDistanceAndSequence
import hu.raven.puppet.logic.step.evolutionary.common.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.step.evolutionary.common.initializePopulation.InitializePopulationByModuloStepper
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.modules.FilePathVariableNames.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val commonModule = module {

    single(named(INPUT_FOLDER)) { "D:\\Git\\GitHub\\SourceCodes\\Kotlin\\JVM\\BacterialMemeticConsole\\input" }
    single(named(GRAPH_FILE)) { "hungarianExample\\graph.json" }
    single(named(EDGES_BETWEEN_FILE)) { "hungarianExample\\between.json" }
    single(named(EDGES_FROM_CENTER_FILE)) { "hungarianExample\\fromCenter.json" }
    single(named(EDGES_TO_CENTER_FILE)) { "hungarianExample\\toCenter.json" }
    single(named(OBJECTIVES_FILE)) { "hungarianExample\\objectives.json" }
    single(named(SALESMAN_FILE)) { "hungarianExample\\salesman.json" }
    single(named(OUTPUT_FOLDER)) { "D:\\Git\\GitHub\\SourceCodes\\Kotlin\\JVM\\BacterialMemeticConsole\\output" }

    single {
        DoubleLogger()
    }

    single {
        CSVLogger()
    }

    single {
        VRPTaskHolder()
    }

    factory<Diversity<*>> {
        DiversityByInnerDistanceAndSequence<DOnePartRepresentation>()
    }

    factory<SSpecimenRepresentationFactory<*>> {
        OnePartRepresentationFactory()
    }

    factory<InitializePopulation<*>> {
        InitializePopulationByModuloStepper<DOnePartRepresentation>()
    }

    factory {
        OrderPopulationByCost<DOnePartRepresentation>()
    }
    factory<CalculateCost<*>> {
        CalculateCostOfVRPSolutionWithoutCapacity<DOnePartRepresentation>()
    }
    factory { CalculateCostOfEdge() }
    factory { CalculateCostOfObjective() }

    factory<Boost<*>> {
        BoostOnBestLazy<DOnePartRepresentation>()
    }
    factory<BoostOperator<*>> {
        Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit<DOnePartRepresentation>(
            stepLimit = 2000
        )
    }


}