package hu.raven.puppet.modules

import hu.raven.puppet.logic.logging.CSVLogger
import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.specimen.DOnePartRepresentation
import hu.raven.puppet.logic.specimen.factory.OnePartRepresentationFactory
import hu.raven.puppet.logic.specimen.factory.SSpecimenRepresentationFactory
import hu.raven.puppet.logic.step.common.calculatecostofedge.CalculateCostOfEdge
import hu.raven.puppet.logic.step.common.calculatecostofobjective.CalculateCostOfObjective
import hu.raven.puppet.logic.step.evolutionary.common.OrderPopulationByCost
import hu.raven.puppet.logic.step.evolutionary.common.boost.Boost
import hu.raven.puppet.logic.step.evolutionary.common.boost.BoostOnBestLazy
import hu.raven.puppet.logic.step.evolutionary.common.boostoperator.BoostOperator
import hu.raven.puppet.logic.step.evolutionary.common.boostoperator.Opt2StepWithPerSpecimenProgressMemoryAndRandomOrder
import hu.raven.puppet.logic.step.evolutionary.common.boostoperator.Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit
import hu.raven.puppet.logic.step.evolutionary.common.diversity.Diversity
import hu.raven.puppet.logic.step.evolutionary.common.diversity.DiversityByInnerDistanceAndSequence
import hu.raven.puppet.logic.step.evolutionary.common.initializePopulation.InitializeHugePopulationThanPreOptimizeThanSelectBest
import hu.raven.puppet.logic.step.evolutionary.common.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.step.evolutionary.common.initializePopulation.InitializePopulationByModuloStepper
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.modules.FilePathVariableNames.OUTPUT_FOLDER
import org.koin.core.qualifier.named
import org.koin.dsl.module

val commonModule = module {
    single(named(OUTPUT_FOLDER)) { "D:\\Git\\GitHub\\SourceCodes\\Kotlin\\JVM\\BacterialMemeticConsole\\output\\default" }

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
        InitializeHugePopulationThanPreOptimizeThanSelectBest<DOnePartRepresentation>()
        //InitializePopulationByModuloStepper<DOnePartRepresentation>()
    }

    factory {
        OrderPopulationByCost<DOnePartRepresentation>()
    }
    factory { CalculateCostOfEdge() }
    factory { CalculateCostOfObjective() }

    factory<Boost<*>> {
        BoostOnBestLazy<DOnePartRepresentation>()
    }
    factory<BoostOperator<*>> {
        Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit<DOnePartRepresentation>()
    }


}