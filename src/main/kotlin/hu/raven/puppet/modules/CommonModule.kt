package hu.raven.puppet.modules

import hu.raven.puppet.logic.logging.CSVLogger
import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.boost.Boost
import hu.raven.puppet.logic.step.boost.BoostOnBestLazy
import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.logic.step.boostoperator.Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit
import hu.raven.puppet.logic.step.calculatecostofedge.CalculateCostOfEdge
import hu.raven.puppet.logic.step.calculatecostofobjective.CalculateCostOfObjective
import hu.raven.puppet.logic.step.diversity.Diversity
import hu.raven.puppet.logic.step.diversity.DiversityByInnerDistanceAndSequence
import hu.raven.puppet.logic.step.initializePopulation.InitializeHugePopulationThanPreOptimizeThanSelectBest
import hu.raven.puppet.logic.step.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.factory.OnePartRepresentationFactory
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.modules.FilePathVariableNames.OUTPUT_FOLDER
import org.koin.core.qualifier.named
import org.koin.dsl.module

val commonModule = module {
    single(named(OUTPUT_FOLDER)) { "output/default" }

    single {
        DoubleLogger()
    }

    single {
        CSVLogger()
    }

    single {
        VRPTaskHolder()
    }

    factory<Diversity<*, *>> {
        DiversityByInnerDistanceAndSequence<OnePartRepresentation<Meter>, Meter>()
    }

    factory<SolutionRepresentationFactory<*, *>> {
        OnePartRepresentationFactory()
    }

    factory<InitializePopulation<*, *>> {
        InitializeHugePopulationThanPreOptimizeThanSelectBest<OnePartRepresentation<Meter>, Meter>()
        //InitializePopulationByModuloStepper<DOnePartRepresentation>()
    }

    factory {
        OrderPopulationByCost<OnePartRepresentation<Meter>, Meter>()
    }
    factory { CalculateCostOfEdge() }
    factory { CalculateCostOfObjective() }

    factory<Boost<*, *>> {
        BoostOnBestLazy<OnePartRepresentation<Meter>, Meter>()
    }
    factory<BoostOperator<*, *>> {
        Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit<OnePartRepresentation<Meter>, Meter>()
    }


}